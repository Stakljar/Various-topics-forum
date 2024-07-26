import { useLocation } from "react-router-dom"

export default function Notice() {
  const location = useLocation()
  const text = location.state || "Did you come here by mistake?"

  return (
    <div id="notice">
      <h2>{text}</h2>
    </div>
  )
}
