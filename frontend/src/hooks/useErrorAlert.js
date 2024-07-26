const useErrorAlert = () => {
  const errorAlert = (error) => {
    alert(error.response?.data?.message || error.response?.data?.error || error.response?.data || "Unknown error")
  }

  return errorAlert
}

export default useErrorAlert
