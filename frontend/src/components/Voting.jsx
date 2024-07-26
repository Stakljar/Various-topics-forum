import { useContext } from "react"
import { roles } from "../data/roles"
import { AuthContext } from "../App"

export default function Voting(props) {
  const { user } = useContext(AuthContext)

  return (
    <div className="voting">
      <div>
        {
          user.role !== roles.ADMIN ?
            <svg onClick={() => props.handleClick(true)} viewBox="0 0 24 24" fill={props.isUpvote === true ? "currentColor" : "none"}
              xmlns="http://www.w3.org/2000/svg">
              <path d="M3 12L12 1L21 12L15 12L15 23L9 23L9 12Z" stroke="currentColor" strokeWidth={1.5} strokeLinejoin="round" />
            </svg> : "Upvotes:"
        }
        <span>{props.upvotes}</span>
      </div>
      <div>
        {
          user.role !== roles.ADMIN ?
            <svg onClick={() => props.handleClick(false)} viewBox="0 0 24 24" fill={props.isUpvote === false ? "currentColor" : "none"}
              xmlns="http://www.w3.org/2000/svg">
              <path d="M3 12L12 23L21 12L15 12L15 1L9 1L9 12Z" stroke="currentColor" strokeWidth={1.5} strokeLinejoin="round" />
            </svg> : "Downvotes:"
        }
        <span>{props.downvotes}</span>
      </div>
    </div>
  )
}
