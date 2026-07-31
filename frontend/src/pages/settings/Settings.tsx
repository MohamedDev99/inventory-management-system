import { useEffect } from "react"
import { useNavigate } from "react-router-dom"
import { Controller } from "react-hook-form"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Field, FieldLabel, FieldError } from "@/components/ui/field"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { useSettings } from "./useSettings"

export default function Settings() {
  const navigate = useNavigate()
  const {
    activeTab,
    setActiveTab,
    theme,
    setTheme,
    sidebarDensity,
    setSidebarDensity,
    tableDensity,
    setTableDensity,
    language,
    setLanguage,
    timezone,
    setTimezone,
    dateFormat,
    setDateFormat,
    timeFormat,
    setTimeFormat,
    currency,
    setCurrency,
    currencyPosition,
    setCurrencyPosition,
    notificationPrefs,
    setNotificationPrefs,
    profileForm,
    securityForm,
    businessForm,
    currentUserData,
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
    tabs,
    notificationTypesList,
  } = useSettings()

  // Update profile form default values when user data loads
  useEffect(() => {
    if (currentUserData) {
      profileForm.reset({
        firstName: currentUserData.username || "",
        lastName: "",
        email: currentUserData.email || "",
        phone: "",
        jobTitle: currentUserData.roleName|| "Staff",
        address: "",
        city: "",
        state: "",
        postalCode: "",
      })
    }
  }, [currentUserData, profileForm])

  return (
    <div className="p-6 space-y-6">
      <div className="flex items-center gap-2 text-sm text-muted-foreground">
        <button onClick={() => navigate(-1)} className="hover:text-primary-500 dark:hover:text-primary-400">← Back</button>
        <span>/ Settings</span>
      </div>
      <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Settings</h1>

      <div className="flex flex-col md:flex-row gap-6">
        {/* Left Sidebar */}
        <div className="w-full md:w-60 flex-shrink-0">
          <nav className="space-y-1">
            {tabs.map((tab) => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                  activeTab === tab.id
                    ? "bg-primary-50 dark:bg-primary-900/30 text-primary-600 dark:text-primary-400 border-l-2 border-primary-500"
                    : "text-muted-foreground hover:bg-gray-100 dark:hover:bg-gray-800"
                }`}
              >
                <span>{tab.icon}</span>
                <span>{tab.label}</span>
              </button>
            ))}
          </nav>
        </div>

        {/* Right Content */}
        <div className="flex-1">
          {activeTab === "profile" && (
            <form onSubmit={profileForm.handleSubmit(handleProfileSave)} className="space-y-6">
              <div>
                <h2 className="text-lg font-semibold text-gray-900 dark:text-gray-100">Profile</h2>
                <p className="text-sm text-muted-foreground">Update your personal information and avatar</p>
              </div>

              {/* Avatar Section */}
              <div className="flex items-center gap-6">
                <Avatar className="w-24 h-24">
                  <AvatarImage src="" />
                  <AvatarFallback className="bg-primary-100 dark:bg-primary-900/30 text-primary-600 dark:text-primary-400 text-2xl">JD</AvatarFallback>
                </Avatar>
                <div>
                  <Button variant="outline" size="sm">Change Photo</Button>
                  <p className="text-xs text-muted-foreground mt-1">JPG, PNG. Max 2MB</p>
                </div>
              </div>

              {/* Personal Info */}
              <div className="grid grid-cols-2 gap-4">
                <Controller
                  name="firstName"
                  control={profileForm.control}
                  render={({ field, fieldState }) => (
                    <Field data-invalid={fieldState.invalid}>
                      <FieldLabel htmlFor={field.name}>First Name</FieldLabel>
                      <Input
                        {...field}
                        id={field.name}
                        value={field.value ?? ""}
                        onChange={(e) => field.onChange(e.target.value)}
                        aria-invalid={fieldState.invalid}
                      />
                      {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                    </Field>
                  )}
                />
                <Controller
                  name="lastName"
                  control={profileForm.control}
                  render={({ field, fieldState }) => (
                    <Field data-invalid={fieldState.invalid}>
                      <FieldLabel htmlFor={field.name}>Last Name</FieldLabel>
                      <Input
                        {...field}
                        id={field.name}
                        value={field.value ?? ""}
                        onChange={(e) => field.onChange(e.target.value)}
                        aria-invalid={fieldState.invalid}
                      />
                      {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                    </Field>
                  )}
                />
                <Controller
                  name="email"
                  control={profileForm.control}
                  render={({ field, fieldState }) => (
                    <Field data-invalid={fieldState.invalid}>
                      <FieldLabel htmlFor={field.name}>Email</FieldLabel>
                      <Input
                        {...field}
                        id={field.name}
                        type="email"
                        value={field.value ?? ""}
                        onChange={(e) => field.onChange(e.target.value)}
                        aria-invalid={fieldState.invalid}
                      />
                      {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                    </Field>
                  )}
                />
                <Controller
                  name="phone"
                  control={profileForm.control}
                  render={({ field, fieldState }) => (
                    <Field data-invalid={fieldState.invalid}>
                      <FieldLabel htmlFor={field.name}>Phone</FieldLabel>
                      <Input
                        {...field}
                        id={field.name}
                        value={field.value ?? ""}
                        onChange={(e) => field.onChange(e.target.value)}
                        aria-invalid={fieldState.invalid}
                      />
                    </Field>
                  )}
                />
                <Controller
                  name="jobTitle"
                  control={profileForm.control}
                  render={({ field }) => (
                    <Field>
                      <FieldLabel htmlFor={field.name}>Job Title</FieldLabel>
                      <Input
                        {...field}
                        id={field.name}
                        value={field.value ?? ""}
                        disabled
                      />
                    </Field>
                  )}
                />
              </div>

              {/* Address */}
              <div className="space-y-3">
                <h3 className="font-medium text-gray-900 dark:text-gray-100">Address</h3>
                <Controller
                  name="address"
                  control={profileForm.control}
                  render={({ field }) => (
                    <Field>
                      <FieldLabel htmlFor={field.name}>Address</FieldLabel>
                      <Input
                        {...field}
                        id={field.name}
                        value={field.value ?? ""}
                        onChange={(e) => field.onChange(e.target.value)}
                      />
                    </Field>
                  )}
                />
                <div className="grid grid-cols-3 gap-4">
                  <Controller
                    name="city"
                    control={profileForm.control}
                    render={({ field }) => (
                      <Field>
                        <FieldLabel htmlFor={field.name}>City</FieldLabel>
                        <Input
                          {...field}
                          id={field.name}
                          value={field.value ?? ""}
                          onChange={(e) => field.onChange(e.target.value)}
                        />
                      </Field>
                    )}
                  />
                  <Controller
                    name="state"
                    control={profileForm.control}
                    render={({ field }) => (
                      <Field>
                        <FieldLabel htmlFor={field.name}>State</FieldLabel>
                        <Input
                          {...field}
                          id={field.name}
                          value={field.value ?? ""}
                          onChange={(e) => field.onChange(e.target.value)}
                        />
                      </Field>
                    )}
                  />
                  <Controller
                    name="postalCode"
                    control={profileForm.control}
                    render={({ field }) => (
                      <Field>
                        <FieldLabel htmlFor={field.name}>Pincode</FieldLabel>
                        <Input
                          {...field}
                          id={field.name}
                          value={field.value ?? ""}
                          onChange={(e) => field.onChange(e.target.value)}
                        />
                      </Field>
                    )}
                  />
                </div>
              </div>

              <Button type="submit" className="bg-primary-500 hover:bg-primary-600 dark:bg-primary-500 dark:hover:bg-primary-400">Save Changes</Button>
            </form>
          )}

          {activeTab === "security" && (
            <form onSubmit={securityForm.handleSubmit(handleSecuritySave)} className="space-y-6">
              <div>
                <h2 className="text-lg font-semibold text-gray-900 dark:text-gray-100">Security</h2>
                <p className="text-sm text-muted-foreground">Manage your password and account security</p>
              </div>

              <div className="space-y-4 max-w-md">
                <Controller
                  name="currentPassword"
                  control={securityForm.control}
                  render={({ field, fieldState }) => (
                    <Field data-invalid={fieldState.invalid}>
                      <FieldLabel htmlFor={field.name}>Current Password</FieldLabel>
                      <Input
                        {...field}
                        id={field.name}
                        type="password"
                        value={field.value ?? ""}
                        onChange={(e) => field.onChange(e.target.value)}
                        aria-invalid={fieldState.invalid}
                      />
                      {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                    </Field>
                  )}
                />
                <Controller
                  name="newPassword"
                  control={securityForm.control}
                  render={({ field, fieldState }) => (
                    <Field data-invalid={fieldState.invalid}>
                      <FieldLabel htmlFor={field.name}>New Password</FieldLabel>
                      <Input
                        {...field}
                        id={field.name}
                        type="password"
                        value={field.value ?? ""}
                        onChange={(e) => field.onChange(e.target.value)}
                        aria-invalid={fieldState.invalid}
                      />
                      {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                    </Field>
                  )}
                />
                <Controller
                  name="confirmPassword"
                  control={securityForm.control}
                  render={({ field, fieldState }) => (
                    <Field data-invalid={fieldState.invalid}>
                      <FieldLabel htmlFor={field.name}>Confirm New Password</FieldLabel>
                      <Input
                        {...field}
                        id={field.name}
                        type="password"
                        value={field.value ?? ""}
                        onChange={(e) => field.onChange(e.target.value)}
                        aria-invalid={fieldState.invalid}
                      />
                      {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                    </Field>
                  )}
                />
              </div>

              {/* Password Strength */}
              <div className="space-y-2">
                <div className="h-2 bg-gray-200 dark:bg-gray-700 rounded-full overflow-hidden">
                  <div className="h-full bg-green-500 w-1/2" />
                </div>
                <p className="text-xs text-muted-foreground">Password strength: Fair</p>
              </div>

              <Button type="submit" className="bg-primary-500 hover:bg-primary-600 dark:bg-primary-500 dark:hover:bg-primary-400">Update Password</Button>

              {/* Active Sessions */}
              <div className="pt-6">
                <h3 className="font-medium text-gray-900 dark:text-gray-100">Active Sessions</h3>
                <div className="mt-3 space-y-2">
                  <div className="flex items-center justify-between p-3 border dark:border-gray-700 rounded-lg bg-white dark:bg-gray-800">
                    <div>
                      <p className="font-medium text-gray-900 dark:text-gray-100">Chrome on Windows</p>
                      <p className="text-xs text-muted-foreground">192.168.1.1 • Last active: Just now</p>
                    </div>
                    <Badge className="bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-300">Current</Badge>
                  </div>
                </div>
              </div>
            </form>
          )}

          {activeTab === "notifications" && (
            <div className="space-y-6">
              <div>
                <h2 className="text-lg font-semibold text-gray-900 dark:text-gray-100">Notifications</h2>
                <p className="text-sm text-muted-foreground">Control how and when you receive notifications</p>
              </div>

              {/* Notification Channels */}
              <div className="space-y-4">
                <h3 className="font-medium text-gray-900 dark:text-gray-100">Notification Channels</h3>
                <div className="flex items-center justify-between p-3 border dark:border-gray-700 rounded-lg bg-white dark:bg-gray-800">
                  <div>
                    <p className="font-medium text-gray-900 dark:text-gray-100">Email Notifications</p>
                    <p className="text-xs text-muted-foreground">Receive notifications via email</p>
                  </div>
                  <button
                    onClick={() => handleChannelToggle("email")}
                    className={`w-11 h-6 rounded-full transition-colors ${notificationPrefs.channels.email ? "bg-primary-500" : "bg-gray-300 dark:bg-gray-600"}`}
                  >
                    <span className={`block w-5 h-5 bg-white rounded-full shadow transform transition-transform ${notificationPrefs.channels.email ? "translate-x-5" : "translate-x-0.5"}`} />
                  </button>
                </div>
                <div className="flex items-center justify-between p-3 border dark:border-gray-700 rounded-lg bg-white dark:bg-gray-800">
                  <div>
                    <p className="font-medium text-gray-900 dark:text-gray-100">Push Notifications</p>
                    <p className="text-xs text-muted-foreground">Receive push notifications in browser</p>
                  </div>
                  <button
                    onClick={() => handleChannelToggle("push")}
                    className={`w-11 h-6 rounded-full transition-colors ${notificationPrefs.channels.push ? "bg-primary-500" : "bg-gray-300 dark:bg-gray-600"}`}
                  >
                    <span className={`block w-5 h-5 bg-white rounded-full shadow transform transition-transform ${notificationPrefs.channels.push ? "translate-x-5" : "translate-x-0.5"}`} />
                  </button>
                </div>
                <div className="flex items-center justify-between p-3 border dark:border-gray-700 rounded-lg bg-white dark:bg-gray-800">
                  <div>
                    <p className="font-medium text-gray-900 dark:text-gray-100">SMS Notifications</p>
                    <p className="text-xs text-muted-foreground">US numbers only</p>
                  </div>
                  <button
                    onClick={() => handleChannelToggle("sms")}
                    className={`w-11 h-6 rounded-full transition-colors ${notificationPrefs.channels.sms ? "bg-primary-500" : "bg-gray-300 dark:bg-gray-600"}`}
                  >
                    <span className={`block w-5 h-5 bg-white rounded-full shadow transform transition-transform ${notificationPrefs.channels.sms ? "translate-x-5" : "translate-x-0.5"}`} />
                  </button>
                </div>
              </div>

              {/* Per-Type Notification Preferences */}
              <div className="space-y-4">
                <h3 className="font-medium text-gray-900 dark:text-gray-100">Notification Types</h3>
                <div className="border dark:border-gray-700 rounded-lg overflow-hidden">
                  <Table>
                    <TableHeader>
                      <TableRow className="bg-gray-50 dark:bg-gray-900/50">
                        <TableHead className="dark:text-gray-300">Type</TableHead>
                        <TableHead className="text-center dark:text-gray-300">Enabled</TableHead>
                        <TableHead className="text-center dark:text-gray-300">Email</TableHead>
                        <TableHead className="text-center dark:text-gray-300">Push</TableHead>
                        <TableHead className="text-center dark:text-gray-300">SMS</TableHead>
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {notificationTypesList.map((type) => {
                        const typePrefs = notificationPrefs.types[type.id] || { enabled: true, email: true, push: true, sms: false }
                        return (
                          <TableRow key={type.id} className="dark:border-gray-700">
                            <TableCell className="font-medium dark:text-gray-100">{type.label}</TableCell>
                            <TableCell className="text-center">
                              <button
                                onClick={() => handleTypeToggle(type.id, "enabled")}
                                className={`w-9 h-5 rounded-full transition-colors ${typePrefs.enabled ? "bg-primary-500" : "bg-gray-300 dark:bg-gray-600"}`}
                              >
                                <span className={`block w-4 h-4 bg-white rounded-full shadow transform transition-transform ${typePrefs.enabled ? "translate-x-4" : "translate-x-0.5"}`} />
                              </button>
                            </TableCell>
                            <TableCell className="text-center">
                              <button
                                onClick={() => handleTypeToggle(type.id, "email")}
                                disabled={!isChannelEnabled("email") || !typePrefs.enabled}
                                className={`w-9 h-5 rounded-full transition-colors ${typePrefs.email && isChannelEnabled("email") && typePrefs.enabled ? "bg-primary-500" : "bg-gray-300 dark:bg-gray-600"} ${!isChannelEnabled("email") || !typePrefs.enabled ? "opacity-50 cursor-not-allowed" : ""}`}
                              >
                                <span className={`block w-4 h-4 bg-white rounded-full shadow transform transition-transform ${typePrefs.email && isChannelEnabled("email") && typePrefs.enabled ? "translate-x-4" : "translate-x-0.5"}`} />
                              </button>
                            </TableCell>
                            <TableCell className="text-center">
                              <button
                                onClick={() => handleTypeToggle(type.id, "push")}
                                disabled={!isChannelEnabled("push") || !typePrefs.enabled}
                                className={`w-9 h-5 rounded-full transition-colors ${typePrefs.push && isChannelEnabled("push") && typePrefs.enabled ? "bg-primary-500" : "bg-gray-300 dark:bg-gray-600"} ${!isChannelEnabled("push") || !typePrefs.enabled ? "opacity-50 cursor-not-allowed" : ""}`}
                              >
                                <span className={`block w-4 h-4 bg-white rounded-full shadow transform transition-transform ${typePrefs.push && isChannelEnabled("push") && typePrefs.enabled ? "translate-x-4" : "translate-x-0.5"}`} />
                              </button>
                            </TableCell>
                            <TableCell className="text-center">
                              <button
                                onClick={() => handleTypeToggle(type.id, "sms")}
                                disabled={!isChannelEnabled("sms") || !typePrefs.enabled}
                                className={`w-9 h-5 rounded-full transition-colors ${typePrefs.sms && isChannelEnabled("sms") && typePrefs.enabled ? "bg-primary-500" : "bg-gray-300 dark:bg-gray-600"} ${!isChannelEnabled("sms") || !typePrefs.enabled ? "opacity-50 cursor-not-allowed" : ""}`}
                              >
                                <span className={`block w-4 h-4 bg-white rounded-full shadow transform transition-transform ${typePrefs.sms && isChannelEnabled("sms") && typePrefs.enabled ? "translate-x-4" : "translate-x-0.5"}`} />
                              </button>
                            </TableCell>
                          </TableRow>
                        )
                      })}
                    </TableBody>
                  </Table>
                </div>
              </div>

              {/* Quiet Hours */}
              <div className="space-y-4">
                <h3 className="font-medium text-gray-900 dark:text-gray-100">Quiet Hours</h3>
                <div className="flex items-center justify-between p-3 border dark:border-gray-700 rounded-lg bg-white dark:bg-gray-800">
                  <div>
                    <p className="font-medium text-gray-900 dark:text-gray-100">Enable Quiet Hours</p>
                    <p className="text-xs text-muted-foreground">During quiet hours, push and SMS notifications will be silenced</p>
                  </div>
                  <button
                    onClick={handleQuietHoursToggle}
                    className={`w-11 h-6 rounded-full transition-colors ${notificationPrefs.quietHours?.enabled ? "bg-primary-500" : "bg-gray-300 dark:bg-gray-600"}`}
                  >
                    <span className={`block w-5 h-5 bg-white rounded-full shadow transform transition-transform ${notificationPrefs.quietHours?.enabled ? "translate-x-5" : "translate-x-0.5"}`} />
                  </button>
                </div>
                {notificationPrefs.quietHours?.enabled && (
                  <div className="grid grid-cols-2 gap-4 ml-4">
                    <div className="grid gap-2">
                      <Label className="dark:text-gray-100">Start Time</Label>
                      <Input
                        type="time"
                        value={notificationPrefs.quietHours?.startTime || "22:00"}
                        onChange={(e) => setNotificationPrefs(prev => ({
                          ...prev,
                          quietHours: { ...prev.quietHours!, startTime: e.target.value }
                        }))}
                        className="dark:bg-gray-800 dark:border-gray-700"
                      />
                    </div>
                    <div className="grid gap-2">
                      <Label className="dark:text-gray-100">End Time</Label>
                      <Input
                        type="time"
                        value={notificationPrefs.quietHours?.endTime || "08:00"}
                        onChange={(e) => setNotificationPrefs(prev => ({
                          ...prev,
                          quietHours: { ...prev.quietHours!, endTime: e.target.value }
                        }))}
                        className="dark:bg-gray-800 dark:border-gray-700"
                      />
                    </div>
                  </div>
                )}
              </div>

              <Button 
                className="bg-primary-500 hover:bg-primary-600 dark:bg-primary-500 dark:hover:bg-primary-400" 
                onClick={handleNotificationSave}
              >
                Save Preferences
              </Button>
            </div>
          )}

          {activeTab === "appearance" && (
            <div className="space-y-6">
              <div>
                <h2 className="text-lg font-semibold text-gray-900 dark:text-gray-100">Appearance</h2>
                <p className="text-sm text-muted-foreground">Customize the look and feel of your workspace</p>
              </div>

              <div className="space-y-4">
                <h3 className="font-medium text-gray-900 dark:text-gray-100">Color Theme</h3>
                <div className="grid grid-cols-3 gap-4">
                  {[
                    { id: "light", label: "Light", preview: "bg-white border" },
                    { id: "dark", label: "Dark", preview: "bg-gray-800" },
                    { id: "system", label: "System", preview: "bg-gradient-to-r from-white to-gray-800" }
                  ].map((t) => (
                    <div 
                      key={t.id}
                      onClick={() => setTheme(t.id)}
                      className={`p-4 border rounded-lg cursor-pointer bg-white dark:bg-gray-800 ${theme === t.id ? "border-primary-500 ring-2 ring-primary-200 dark:ring-primary-800" : "dark:border-gray-700"}`}
                    >
                      <div className={`h-20 rounded mb-2 ${t.preview}`} />
                      <p className="text-sm font-medium text-center text-gray-900 dark:text-gray-100">{t.label}</p>
                    </div>
                  ))}
                </div>
              </div>

              <div className="space-y-4">
                <h3 className="font-medium text-gray-900 dark:text-gray-100">Sidebar Style</h3>
                <div className="flex gap-4">
                  {[
                    { id: "comfortable", label: "Comfortable", desc: "Wider spacing" },
                    { id: "compact", label: "Compact", desc: "Tighter spacing" }
                  ].map((opt) => (
                    <div 
                      key={opt.id}
                      onClick={() => setSidebarDensity(opt.id)}
                      className={`flex-1 p-4 border rounded-lg cursor-pointer bg-white dark:bg-gray-800 ${sidebarDensity === opt.id ? "border-primary-500 ring-2 ring-primary-200 dark:ring-primary-800" : "dark:border-gray-700"}`}
                    >
                      <p className="font-medium text-gray-900 dark:text-gray-100">{opt.label}</p>
                      <p className="text-xs text-muted-foreground">{opt.desc}</p>
                    </div>
                  ))}
                </div>
              </div>

              <div className="space-y-4">
                <h3 className="font-medium text-gray-900 dark:text-gray-100">Table Row Height</h3>
                <div className="flex gap-4">
                  {[
                    { id: "default", label: "Default" },
                    { id: "compact", label: "Compact" },
                    { id: "spacious", label: "Spacious" }
                  ].map((opt) => (
                    <div 
                      key={opt.id}
                      onClick={() => setTableDensity(opt.id)}
                      className={`flex-1 p-3 border rounded-lg cursor-pointer bg-white dark:bg-gray-800 text-center ${tableDensity === opt.id ? "border-primary-500 ring-2 ring-primary-200 dark:ring-primary-800" : "dark:border-gray-700"}`}
                    >
                      <p className="text-sm font-medium text-gray-900 dark:text-gray-100">{opt.label}</p>
                    </div>
                  ))}
                </div>
              </div>

              <Button className="bg-primary-500 hover:bg-primary-600 dark:bg-primary-500 dark:hover:bg-primary-400" onClick={handleAppearanceSave}>Save Appearance</Button>
            </div>
          )}

          {activeTab === "language" && (
            <div className="space-y-6">
              <div>
                <h2 className="text-lg font-semibold text-gray-900 dark:text-gray-100">Language & Region</h2>
                <p className="text-sm text-muted-foreground">Set your language, timezone, and date preferences</p>
              </div>

              <div className="grid gap-6 max-w-md">
                <div className="grid gap-2">
                  <Label className="dark:text-gray-100">Language</Label>
                  <Select value={language} onValueChange={setLanguage}>
                    <SelectTrigger className="dark:bg-gray-800 dark:border-gray-700 dark:text-gray-100"><SelectValue /></SelectTrigger>
                    <SelectContent className="dark:bg-gray-800 dark:border-gray-700">
                      <SelectItem value="en" className="dark:text-gray-100">English</SelectItem>
                    </SelectContent>
                  </Select>
                  <p className="text-xs text-muted-foreground">More languages coming soon</p>
                </div>
                
                <div className="grid gap-2">
                  <Label className="dark:text-gray-100">Timezone</Label>
                  <Select value={timezone} onValueChange={setTimezone}>
                    <SelectTrigger className="dark:bg-gray-800 dark:border-gray-700 dark:text-gray-100"><SelectValue /></SelectTrigger>
                    <SelectContent className="dark:bg-gray-800 dark:border-gray-700">
                      <SelectItem value="America/New_York" className="dark:text-gray-100">America/New_York (EST)</SelectItem>
                      <SelectItem value="America/Los_Angeles" className="dark:text-gray-100">America/Los_Angeles (PST)</SelectItem>
                      <SelectItem value="America/Chicago" className="dark:text-gray-100">America/Chicago (CST)</SelectItem>
                      <SelectItem value="Europe/London" className="dark:text-gray-100">Europe/London (GMT)</SelectItem>
                      <SelectItem value="Europe/Paris" className="dark:text-gray-100">Europe/Paris (CET)</SelectItem>
                      <SelectItem value="Asia/Tokyo" className="dark:text-gray-100">Asia/Tokyo (JST)</SelectItem>
                      <SelectItem value="Asia/Kolkata" className="dark:text-gray-100">Asia/Kolkata (IST)</SelectItem>
                    </SelectContent>
                  </Select>
                  <p className="text-xs text-muted-foreground">Current time: {new Date().toLocaleTimeString()}</p>
                </div>
                
                <div className="grid gap-2">
                  <Label className="dark:text-gray-100">Date Format</Label>
                  <Select value={dateFormat} onValueChange={setDateFormat}>
                    <SelectTrigger className="dark:bg-gray-800 dark:border-gray-700 dark:text-gray-100"><SelectValue /></SelectTrigger>
                    <SelectContent className="dark:bg-gray-800 dark:border-gray-700">
                      <SelectItem value="MM/DD/YYYY" className="dark:text-gray-100">MM/DD/YYYY</SelectItem>
                      <SelectItem value="DD/MM/YYYY" className="dark:text-gray-100">DD/MM/YYYY</SelectItem>
                      <SelectItem value="YYYY-MM-DD" className="dark:text-gray-100">YYYY-MM-DD</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
                
                <div className="grid gap-2">
                  <Label className="dark:text-gray-100">Time Format</Label>
                  <Select value={timeFormat} onValueChange={setTimeFormat}>
                    <SelectTrigger className="dark:bg-gray-800 dark:border-gray-700 dark:text-gray-100"><SelectValue /></SelectTrigger>
                    <SelectContent className="dark:bg-gray-800 dark:border-gray-700">
                      <SelectItem value="12-hour" className="dark:text-gray-100">12-hour (AM/PM)</SelectItem>
                      <SelectItem value="24-hour" className="dark:text-gray-100">24-hour</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
                
                <div className="grid gap-2">
                  <Label className="dark:text-gray-100">Currency</Label>
                  <Select value={currency} onValueChange={setCurrency}>
                    <SelectTrigger className="dark:bg-gray-800 dark:border-gray-700 dark:text-gray-100"><SelectValue /></SelectTrigger>
                    <SelectContent className="dark:bg-gray-800 dark:border-gray-700">
                      <SelectItem value="USD" className="dark:text-gray-100">USD ($)</SelectItem>
                      <SelectItem value="EUR" className="dark:text-gray-100">EUR (€)</SelectItem>
                      <SelectItem value="GBP" className="dark:text-gray-100">GBP (£)</SelectItem>
                      <SelectItem value="INR" className="dark:text-gray-100">INR (₹)</SelectItem>
                      <SelectItem value="JPY" className="dark:text-gray-100">JPY (¥)</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
                
                <div className="grid gap-2">
                  <Label className="dark:text-gray-100">Currency Position</Label>
                  <Select value={currencyPosition} onValueChange={setCurrencyPosition}>
                    <SelectTrigger className="dark:bg-gray-800 dark:border-gray-700 dark:text-gray-100"><SelectValue /></SelectTrigger>
                    <SelectContent className="dark:bg-gray-800 dark:border-gray-700">
                      <SelectItem value="before" className="dark:text-gray-100">Before amount ($100)</SelectItem>
                      <SelectItem value="after" className="dark:text-gray-100">After amount (100$)</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
              </div>

              <Button className="bg-primary-500 hover:bg-primary-600 dark:bg-primary-500 dark:hover:bg-primary-400" onClick={handleLanguageSave}>Save Preferences</Button>
            </div>
          )}

          {activeTab === "business" && (
            <form onSubmit={businessForm.handleSubmit(handleBusinessSave)} className="space-y-6">
              <div>
                <h2 className="text-lg font-semibold text-gray-900 dark:text-gray-100">Business Settings</h2>
                <p className="text-sm text-muted-foreground">Configure your company profile and system-wide defaults</p>
              </div>

              <div className="grid gap-4 max-w-md">
                <Controller
                  name="companyName"
                  control={businessForm.control}
                  render={({ field, fieldState }) => (
                    <Field data-invalid={fieldState.invalid}>
                      <FieldLabel htmlFor={field.name}>Company Name</FieldLabel>
                      <Input
                        {...field}
                        id={field.name}
                        placeholder="MoeWare Inc."
                        value={field.value ?? ""}
                        onChange={(e) => field.onChange(e.target.value)}
                        aria-invalid={fieldState.invalid}
                      />
                    </Field>
                  )}
                />
                <Controller
                  name="industry"
                  control={businessForm.control}
                  render={({ field }) => (
                    <Field>
                      <FieldLabel htmlFor={field.name}>Industry</FieldLabel>
                      <Select
                        value={field.value || ""}
                        onValueChange={field.onChange}
                      >
                        <SelectTrigger id={field.name}>
                          <SelectValue placeholder="Select industry" />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="retail">Retail</SelectItem>
                          <SelectItem value="manufacturing">Manufacturing</SelectItem>
                        </SelectContent>
                      </Select>
                    </Field>
                  )}
                />
                <Controller
                  name="businessType"
                  control={businessForm.control}
                  render={({ field }) => (
                    <Field>
                      <FieldLabel htmlFor={field.name}>Business Type</FieldLabel>
                      <Select
                        value={field.value || ""}
                        onValueChange={field.onChange}
                      >
                        <SelectTrigger id={field.name}>
                          <SelectValue placeholder="Select type" />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="retailer">Retailer</SelectItem>
                          <SelectItem value="distributor">Distributor</SelectItem>
                        </SelectContent>
                      </Select>
                    </Field>
                  )}
                />
              </div>

              <Button type="submit" className="bg-primary-500 hover:bg-primary-600 dark:bg-primary-500 dark:hover:bg-primary-400">Save Business Settings</Button>
            </form>
          )}

          {activeTab === "users" && (
            <div className="space-y-6">
              <div className="flex items-center justify-between">
                <div>
                  <h2 className="text-lg font-semibold text-gray-900 dark:text-gray-100">User Management</h2>
                  <p className="text-sm text-muted-foreground">Manage system users and their access roles</p>
                </div>
                <Button className="bg-primary-500 hover:bg-primary-600 dark:bg-primary-500 dark:hover:bg-primary-400">+ Invite User</Button>
              </div>

              <div className="border dark:border-gray-700 rounded-lg overflow-hidden bg-white dark:bg-gray-800">
                <table className="w-full">
                  <thead className="bg-gray-50 dark:bg-gray-900/50">
                    <tr>
                      <th className="px-4 py-3 text-left text-sm font-medium text-gray-900 dark:text-gray-100">User</th>
                      <th className="px-4 py-3 text-left text-sm font-medium text-gray-900 dark:text-gray-100">Role</th>
                      <th className="px-4 py-3 text-left text-sm font-medium text-gray-900 dark:text-gray-100">Status</th>
                      <th className="px-4 py-3 text-left text-sm font-medium text-gray-900 dark:text-gray-100">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr className="border-t dark:border-gray-700">
                      <td className="px-4 py-3">
                        <div className="flex items-center gap-3">
                          <Avatar className="h-8 w-8"><AvatarFallback className="dark:bg-gray-700 dark:text-gray-100">JD</AvatarFallback></Avatar>
                          <div>
                            <p className="font-medium text-gray-900 dark:text-gray-100">John Doe</p>
                            <p className="text-xs text-muted-foreground">john@moeware.com</p>
                          </div>
                        </div>
                      </td>
                      <td className="px-4 py-3"><Badge className="bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-300">Admin</Badge></td>
                      <td className="px-4 py-3"><Badge className="bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-300">Active</Badge></td>
                      <td className="px-4 py-3"><Button variant="ghost" size="sm" className="dark:text-gray-100">Edit</Button></td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          )}

          {activeTab === "audit" && (
            <div className="space-y-6">
              <div>
                <h2 className="text-lg font-semibold text-gray-900 dark:text-gray-100">Audit Log</h2>
                <p className="text-sm text-muted-foreground">Track all system actions and changes</p>
              </div>

              <div className="border dark:border-gray-700 rounded-lg overflow-hidden bg-white dark:bg-gray-800">
                <table className="w-full">
                  <thead className="bg-gray-50 dark:bg-gray-900/50">
                    <tr>
                      <th className="px-4 py-3 text-left text-sm font-medium text-gray-900 dark:text-gray-100">Timestamp</th>
                      <th className="px-4 py-3 text-left text-sm font-medium text-gray-900 dark:text-gray-100">User</th>
                      <th className="px-4 py-3 text-left text-sm font-medium text-gray-900 dark:text-gray-100">Action</th>
                      <th className="px-4 py-3 text-left text-sm font-medium text-gray-900 dark:text-gray-100">Entity</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr className="border-t dark:border-gray-700">
                      <td className="px-4 py-3 text-sm text-gray-900 dark:text-gray-100">2026-02-26 10:30:00</td>
                      <td className="px-4 py-3 text-gray-900 dark:text-gray-100">John Doe</td>
                      <td className="px-4 py-3"><Badge className="bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-300">CREATE</Badge></td>
                      <td className="px-4 py-3 text-gray-900 dark:text-gray-100">Product</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
