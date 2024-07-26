export default function Button({ text, onClick, type }) {
  return (
    <button className="default-button" type={type || "button"} onClick={onClick}>{text}</button>
  )
}
