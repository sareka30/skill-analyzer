export function extractErrorMessage(error, fallback = 'Something went wrong. Please try again.') {
  const data = error?.response?.data
  if (!data) return error?.message || fallback
  if (data.fieldErrors) {
    const firstField = Object.values(data.fieldErrors)[0]
    if (firstField) return firstField
  }
  return data.message || fallback
}
