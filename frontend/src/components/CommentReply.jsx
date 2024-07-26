import { useNavigate } from "react-router-dom"

export default function CommentReply(props) {
  const navigate = useNavigate()

  return (
    <div className="comment-reply">
      <div>
        <div>Reply to&nbsp;
          <span className="comment-reply-comment-author" onClick={() => props.userId && navigate(`/profile/${props.userId}`)}>
            {props.deletedAt ? "" : props.userId ? props.userProfileName : "Deleted user" }
          </span>: 
        </div>
        {props.deletedAt ? "Deleted comment " : props.content.length > 80 ? props.content.substring(0, 80) + "..." : props.content}
      </div>
      {
        props.cancelReply &&
        <svg onClick={props.cancelReply} viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M1 1 L23 23 M1 23 L23 1" stroke="#c53d3d" strokeWidth="3" />
        </svg>
      }
    </div>
  )
}
