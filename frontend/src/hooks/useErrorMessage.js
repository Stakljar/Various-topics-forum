import { useState } from "react"

const useErrorMessage = () => {
  const [errorMessage, setErrorMessage] = useState("")

  const handleError = (error) => {
    setErrorMessage(error.response?.data?.message || error.response?.data?.error || error.response?.data || "Unknown error")
  }

  const resetError = () => {
    setErrorMessage("")
  }

  return [errorMessage, handleError, resetError, setErrorMessage]
}

export default useErrorMessage
