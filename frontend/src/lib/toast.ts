import toast from "react-hot-toast"

// Toast helper functions with consistent styling
export const toastSuccess = (message: string) => {
  toast.success(message, {
    duration: 3000,
    style: {
      background: "#0f172a",
      color: "#f8fafc",
    },
  })
}

export const toastError = (message: string) => {
  toast.error(message, {
    duration: 5000, // Longer for errors
    style: {
      background: "#0f172a",
      color: "#f8fafc",
    },
  })
}

export const toastInfo = (message: string) => {
  toast(message, {
    duration: 3000,
    icon: "ℹ️",
    style: {
      background: "#0f172a",
      color: "#f8fafc",
    },
  })
}

export const toastWarning = (message: string) => {
  toast(message, {
    duration: 4000,
    icon: "⚠️",
    style: {
      background: "#0f172a",
      color: "#f8fafc",
    },
  })
}

export const toastLoading = (message: string) => {
  return toast.loading(message, {
    style: {
      background: "#0f172a",
      color: "#f8fafc",
    },
  })
}

export const toastDismiss = () => {
  toast.dismiss()
}
