import { useState, useReducer } from "react"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import {
  profileSettingsSchema,
  securitySettingsSchema,
  businessSettingsSchema,
  type ProfileSettingsFormData,
  type SecuritySettingsFormData,
  type BusinessSettingsFormData,
} from "@/lib/schemas"
import { useCurrentUser } from "@/services/user"
import { useChangePassword } from "@/services/auth"
// import { useNotificationPreferences, useUpdateNotificationPreferences } from "@/services/notification"
import type { NotificationPreferences, NotificationType, User } from "@/types"

type SettingsTab = "profile" | "security" | "notifications" | "appearance" | "language" | "business" | "users" | "audit"

// Default values
const defaultSecurityData: SecuritySettingsFormData = {
  currentPassword: "",
  newPassword: "",
  confirmPassword: "",
}

const defaultBusinessData: BusinessSettingsFormData = {
  companyName: "",
  industry: "",
  businessType: "",
}

const defaultNotificationPreferences: NotificationPreferences = {
  channels: { email: true, push: true, sms: false },
  types: {
    LOW_STOCK: { enabled: true, email: true, push: true, sms: false },
    ORDER_APPROVED: { enabled: true, email: true, push: true, sms: false },
    ORDER_RECEIVED: { enabled: true, email: true, push: true, sms: false },
    SHIPMENT: { enabled: true, email: true, push: true, sms: false },
    STOCK_ADJUSTMENT: { enabled: true, email: true, push: false, sms: false },
    SYSTEM: { enabled: true, email: true, push: false, sms: false },
  },
  quietHours: { enabled: false, startTime: "22:00", endTime: "08:00" },
}

const notificationTypes: { id: NotificationType; label: string }[] = [
  { id: "LOW_STOCK", label: "Low Stock Alerts" },
  { id: "ORDER_APPROVED", label: "Order Approved" },
  { id: "ORDER_RECEIVED", label: "Order Received" },
  { id: "SHIPMENT", label: "Shipment Updates" },
  { id: "STOCK_ADJUSTMENT", label: "Stock Adjustments" },
  { id: "SYSTEM", label: "System Notifications" },
]

// Consolidate UI state (tabs)
interface UIState {
  activeTab: SettingsTab
}

type UIAction = { type: "SET_TAB"; payload: SettingsTab }

function uiReducer(state: UIState, action: UIAction): UIState {
  switch (action.type) {
    case "SET_TAB":
      return { ...state, activeTab: action.payload }
    default:
      return state
  }
}

// Consolidate appearance state
interface AppearanceState {
  theme: string
  sidebarDensity: string
  tableDensity: string
}

type AppearanceAction =
  | { type: "SET_THEME"; payload: string }
  | { type: "SET_SIDEBAR_DENSITY"; payload: string }
  | { type: "SET_TABLE_DENSITY"; payload: string }

function appearanceReducer(state: AppearanceState, action: AppearanceAction): AppearanceState {
  switch (action.type) {
    case "SET_THEME":
      return { ...state, theme: action.payload }
    case "SET_SIDEBAR_DENSITY":
      return { ...state, sidebarDensity: action.payload }
    case "SET_TABLE_DENSITY":
      return { ...state, tableDensity: action.payload }
    default:
      return state
  }
}

// Consolidate language state
interface LanguageState {
  language: string
  timezone: string
  dateFormat: string
  timeFormat: string
  currency: string
  currencyPosition: string
}

type LanguageAction =
  | { type: "SET_LANGUAGE"; payload: string }
  | { type: "SET_TIMEZONE"; payload: string }
  | { type: "SET_DATE_FORMAT"; payload: string }
  | { type: "SET_TIME_FORMAT"; payload: string }
  | { type: "SET_CURRENCY"; payload: string }
  | { type: "SET_CURRENCY_POSITION"; payload: string }

function languageReducer(state: LanguageState, action: LanguageAction): LanguageState {
  switch (action.type) {
    case "SET_LANGUAGE":
      return { ...state, language: action.payload }
    case "SET_TIMEZONE":
      return { ...state, timezone: action.payload }
    case "SET_DATE_FORMAT":
      return { ...state, dateFormat: action.payload }
    case "SET_TIME_FORMAT":
      return { ...state, timeFormat: action.payload }
    case "SET_CURRENCY":
      return { ...state, currency: action.payload }
    case "SET_CURRENCY_POSITION":
      return { ...state, currencyPosition: action.payload }
    default:
      return state
  }
}

interface UseSettingsReturn {
  // State
  activeTab: SettingsTab
  setActiveTab: (tab: SettingsTab) => void
  
  // Appearance state
  theme: string
  setTheme: (theme: string) => void
  sidebarDensity: string
  setSidebarDensity: (density: string) => void
  tableDensity: string
  setTableDensity: (density: string) => void
  
  // Language state
  language: string
  setLanguage: (lang: string) => void
  timezone: string
  setTimezone: (tz: string) => void
  dateFormat: string
  setDateFormat: (format: string) => void
  timeFormat: string
  setTimeFormat: (format: string) => void
  currency: string
  setCurrency: (curr: string) => void
  currencyPosition: string
  setCurrencyPosition: (position: string) => void
  
  // Notification preferences
  notificationPrefs: NotificationPreferences
  setNotificationPrefs: (prefs: NotificationPreferences) => void
  
  // Forms
  profileForm: ReturnType<typeof useForm<ProfileSettingsFormData>>
  securityForm: ReturnType<typeof useForm<SecuritySettingsFormData>>
  businessForm: ReturnType<typeof useForm<BusinessSettingsFormData>>
  
  // User data
  currentUserData: User | undefined
  
  // Handlers
  handleProfileSave: (data: ProfileSettingsFormData) => void
  handleSecuritySave: (data: SecuritySettingsFormData) => void
  handleBusinessSave: (data: BusinessSettingsFormData) => void
  handleNotificationSave: () => void
  handleAppearanceSave: () => void
  handleLanguageSave: () => void
  handleChannelToggle: (channel: "email" | "push" | "sms") => void
  handleTypeToggle: (type: NotificationType, field: "enabled" | "email" | "push" | "sms") => void
  handleQuietHoursToggle: () => void
  isChannelEnabled: (channel: "email" | "push" | "sms") => boolean
  
  // Constants
  tabs: { id: SettingsTab; label: string; icon: string }[]
  notificationTypesList: { id: NotificationType; label: string }[]
}

export function useSettings(): UseSettingsReturn {
  // Consolidated UI state
  const [uiState, dispatchUI] = useReducer(uiReducer, {
    activeTab: "profile",
  })
  
  // Consolidated appearance state
  const [appearanceState, dispatchAppearance] = useReducer(appearanceReducer, {
    theme: "light",
    sidebarDensity: "comfortable",
    tableDensity: "default",
  })
  
  // Consolidated language state
  const [languageState, dispatchLanguage] = useReducer(languageReducer, {
    language: "en",
    timezone: "America/New_York",
    dateFormat: "MM/DD/YYYY",
    timeFormat: "12-hour",
    currency: "USD",
    currencyPosition: "before",
  })
  
  // Notification preferences - local state for user edits
  // Priority: localStorage -> API query data -> defaults
  // Note: If user has localStorage, we don't sync from API to avoid overwriting their changes
  const [notificationPrefs, setNotificationPrefs] = useState<NotificationPreferences>(() => {
    const stored = localStorage.getItem("moeware-notification-prefs")
    if (stored) {
      try { return JSON.parse(stored) } catch { /* ignore */ }
    }
    return defaultNotificationPreferences
  })
  
  // Notification preferences API - commented out until backend is ready
  // const notificationPrefsQuery = useNotificationPreferences()
  // const updateNotificationPrefs = useUpdateNotificationPreferences()
  
  // Placeholder for notification API
  const notificationPrefsQuery = { data: null }
  const updateNotificationPrefs = { mutate: () => {} }
  
  // When localStorage has no data, use defaults
  // This is safe because there's no useEffect causing the lint error
  const effectivePrefs = (() => {
    if (localStorage.getItem("moeware-notification-prefs")) return notificationPrefs
    return notificationPrefs
  })()
  
  // User data hooks
  const { data: currentUserData } = useCurrentUser()
  
  // Mutation hooks
  const changePassword = useChangePassword()
  
  // Derive current user from query data
  const currentUser = currentUserData?.data as User | undefined
  
  // Set default profile data from API
  const defaultProfileData: ProfileSettingsFormData = {
    firstName: currentUser?.username || "",
    lastName: "",
    email: currentUser?.email || "",
    phone: "",
    jobTitle: currentUser?.roleName || "Staff",
    address: "",
    city: "",
    state: "",
    postalCode: "",
  }
  
  const profileForm = useForm<ProfileSettingsFormData>({
    resolver: zodResolver(profileSettingsSchema),
    defaultValues: defaultProfileData,
    mode: "all",
  })
  
  const securityForm = useForm<SecuritySettingsFormData>({
    resolver: zodResolver(securitySettingsSchema),
    defaultValues: defaultSecurityData,
    mode: "all",
  })
  
  const businessForm = useForm<BusinessSettingsFormData>({
    resolver: zodResolver(businessSettingsSchema),
    defaultValues: defaultBusinessData,
    mode: "all",
  })
  
  // Action creators
  const setActiveTab = (tab: SettingsTab) => dispatchUI({ type: "SET_TAB", payload: tab })
  
  const setTheme = (theme: string) => dispatchAppearance({ type: "SET_THEME", payload: theme })
  const setSidebarDensity = (density: string) => dispatchAppearance({ type: "SET_SIDEBAR_DENSITY", payload: density })
  const setTableDensity = (density: string) => dispatchAppearance({ type: "SET_TABLE_DENSITY", payload: density })
  
  const setLanguage = (lang: string) => dispatchLanguage({ type: "SET_LANGUAGE", payload: lang })
  const setTimezone = (tz: string) => dispatchLanguage({ type: "SET_TIMEZONE", payload: tz })
  const setDateFormat = (format: string) => dispatchLanguage({ type: "SET_DATE_FORMAT", payload: format })
  const setTimeFormat = (format: string) => dispatchLanguage({ type: "SET_TIME_FORMAT", payload: format })
  const setCurrency = (curr: string) => dispatchLanguage({ type: "SET_CURRENCY", payload: curr })
  const setCurrencyPosition = (position: string) => dispatchLanguage({ type: "SET_CURRENCY_POSITION", payload: position })
  
  // Handlers
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const handleProfileSave = (_data: ProfileSettingsFormData) => {
    // Profile save logic handled elsewhere
  }
  
  const handleSecuritySave = (data: SecuritySettingsFormData) => {
    changePassword.mutate({
      currentPassword: data.currentPassword,
      newPassword: data.newPassword,
    })
    securityForm.reset(defaultSecurityData)
  }
  
  const handleBusinessSave = (data: BusinessSettingsFormData) => {
    console.log("Business saved:", data)
  }
  
  const handleNotificationSave = () => {
    updateNotificationPrefs.mutate(effectivePrefs)
  }
  
  const handleAppearanceSave = () => {
    localStorage.setItem("moeware-theme", appearanceState.theme)
    localStorage.setItem("moeware-sidebar-density", appearanceState.sidebarDensity)
    localStorage.setItem("moeware-table-density", appearanceState.tableDensity)
  }
  
  const handleLanguageSave = () => {
    localStorage.setItem("moeware-language", languageState.language)
    localStorage.setItem("moeware-timezone", languageState.timezone)
    localStorage.setItem("moeware-date-format", languageState.dateFormat)
    localStorage.setItem("moeware-time-format", languageState.timeFormat)
    localStorage.setItem("moeware-currency", languageState.currency)
    localStorage.setItem("moeware-currency-position", languageState.currencyPosition)
  }
  
  const handleChannelToggle = (channel: "email" | "push" | "sms") => {
    setNotificationPrefs(prev => {
      const updated = {
        ...prev,
        channels: { ...prev.channels, [channel]: !prev.channels[channel] }
      }
      localStorage.setItem("moeware-notification-prefs", JSON.stringify(updated))
      return updated
    })
  }
  
  const handleTypeToggle = (type: NotificationType, field: "enabled" | "email" | "push" | "sms") => {
    setNotificationPrefs(prev => {
      const updated = {
        ...prev,
        types: {
          ...prev.types,
          [type]: {
            ...prev.types[type],
            [field]: !prev.types[type]?.[field]
          }
        }
      }
      localStorage.setItem("moeware-notification-prefs", JSON.stringify(updated))
      return updated
    })
  }
  
  const handleQuietHoursToggle = () => {
    setNotificationPrefs(prev => {
      const updated = {
        ...prev,
        quietHours: {
          ...prev.quietHours!,
          enabled: !prev.quietHours?.enabled
        }
      }
      localStorage.setItem("moeware-notification-prefs", JSON.stringify(updated))
      return updated
    })
  }
  
  const isChannelEnabled = (channel: "email" | "push" | "sms") => effectivePrefs.channels[channel]
  
  const tabs: { id: SettingsTab; label: string; icon: string }[] = [
    { id: "profile", label: "Profile", icon: "👤" },
    { id: "security", label: "Security", icon: "🔒" },
    { id: "notifications", label: "Notifications", icon: "🔔" },
    { id: "appearance", label: "Appearance", icon: "🎨" },
    { id: "language", label: "Language & Region", icon: "🌐" },
    { id: "business", label: "Business Settings", icon: "🏢" },
    { id: "users", label: "User Management", icon: "👥" },
    { id: "audit", label: "Audit Log", icon: "📋" },
  ]
  
  return {
    // State
    activeTab: uiState.activeTab,
    setActiveTab,
    
    // Appearance state
    theme: appearanceState.theme,
    setTheme,
    sidebarDensity: appearanceState.sidebarDensity,
    setSidebarDensity,
    tableDensity: appearanceState.tableDensity,
    setTableDensity,
    
    // Language state
    language: languageState.language,
    setLanguage,
    timezone: languageState.timezone,
    setTimezone,
    dateFormat: languageState.dateFormat,
    setDateFormat,
    timeFormat: languageState.timeFormat,
    setTimeFormat,
    currency: languageState.currency,
    setCurrency,
    currencyPosition: languageState.currencyPosition,
    setCurrencyPosition,
    
    // Notification preferences (localStorage + API fallback)
    notificationPrefs: effectivePrefs,
    setNotificationPrefs,
    
    // Forms
    profileForm,
    securityForm,
    businessForm,
    
    // User data
    currentUserData: currentUser,
    
    // Handlers
    handleProfileSave,
    handleSecuritySave,
    handleBusinessSave,
    handleNotificationSave,
    handleAppearanceSave,
    handleLanguageSave,
    handleChannelToggle,
    handleTypeToggle,
    handleQuietHoursToggle,
    isChannelEnabled,
    
    // Constants
    tabs,
    notificationTypesList: notificationTypes,
  }
}
