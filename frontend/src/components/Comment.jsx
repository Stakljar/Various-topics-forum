import { useContext, useEffect, useRef, useState } from "react"
import { AuthContext } from "../App"
import { roles } from "../data/roles"
import Voting from "./Voting"
import Button from "./Button"
import CommentReply from "./CommentReply"
import { useNavigate } from "react-router-dom"
import useErrorAlert from "../hooks/useErrorAlert"

export default function Comment({ comment, setComments, setComment, interceptedInstance }) {
  const { user } = useContext(AuthContext)
  const isUpvoteRef = useRef(null)
  const navigate = useNavigate()
  const errorAlert = useErrorAlert()
  const [isVoteLoading, setIsVoteLoading] = useState(false)
  const [isDeleteLoading, setIsDeleteLoading] = useState(false)

  useEffect(() => {
    if (!isVoteLoading) {
      return
    }
    const abortController = new AbortController()
    const vote = async () => {
      try {
        const response = await interceptedInstance.post(`/comments/vote`,
          { commentId: comment.id, isUpvote: isUpvoteRef.current },
          {
            headers: {
              "Authorization": "Bearer " + user.accessToken,
            },
            signal: abortController.signal,
          })
        setIsVoteLoading(false)
        const isUpvote = response.data.isUpvote
        setComments((prev) => {
          const modifiedComment = prev.find(c => c.id === comment.id)
          modifiedComment.upvoteCount = isUpvote && !modifiedComment.isUserUpvote ? modifiedComment.upvoteCount + 1 :
            !isUpvote && modifiedComment.isUserUpvote ? modifiedComment.upvoteCount - 1 : modifiedComment.upvoteCount
          modifiedComment.downvoteCount = isUpvote === false && (modifiedComment.isUserUpvote || modifiedComment.isUserUpvote === null) ?
            modifiedComment.downvoteCount + 1 : (isUpvote || isUpvote === null) && modifiedComment.isUserUpvote === false ?
              modifiedComment.downvoteCount - 1 : modifiedComment.downvoteCount
          modifiedComment.isUserUpvote = isUpvote
          return prev
        })
      }
      catch (error) {
        setIsVoteLoading(false)
        errorAlert(error)
      }
    }
    vote()
    return () => abortController.abort()
  }, [isVoteLoading])

  useEffect(() => {
    if (!isDeleteLoading) {
      return
    }
    const abortController = new AbortController()
    const deleteComment = async () => {
      try {
        await interceptedInstance.delete(`/comments/remove/${comment.id}`, {
          signal: abortController.signal,
          headers: {
            "Authorization": user.accessToken,
          },
        })
        setIsDeleteLoading(false)
        setComments((prev) => prev.filter(c => c.id !== comment.id))
      }
      catch (error) {
        setIsDeleteLoading(false)
        errorAlert(error)
      }
    }
    deleteComment()
    return () => abortController.abort()
  }, [isDeleteLoading])

  return (
    <div className="comment">
      <div className="comment-author-and-date">
        <strong className="comment-author" onClick={() => comment.userId && navigate(`/profile/${comment.userId}`)}>
          {comment.deletedAt ? "" : comment.userId ? comment.userProfileName : "Deleted user"}
        </strong>
        <span className="comment-date">{comment.createdAt}</span>
      </div>
      {
        comment.parentCommentId &&
        <CommentReply id={comment.parentCommentId} userId={comment.parentCommentUserId} userProfileName={comment.parentCommentUserProfileName}
          content={comment.parentCommentContent} createdAt={comment.parentCommentCreatedAt} deletedAt={comment.parentCommentDeletedAt} />
      }
      <div className="comment-content">
        {comment.deletedAt ? "Deleted comment" : comment.content}
      </div>
      <div className="comment-reactions">
        {
          !comment.deletedAt &&
          <Voting handleClick={(isUpvote) => {
            if (user.role !== roles.REGULAR_USER) { alert("You need to be signed in to vote.") }
            else { isUpvoteRef.current = isUpvote; setIsVoteLoading(true) }
          }} upvotes={comment.upvoteCount} downvotes={comment.downvoteCount} isUpvote={comment.isUserUpvote} />
        }
        {(user.role === roles.REGULAR_USER && !comment.deletedAt) ? <Button text="Reply"
          onClick={() => setComment((prev) => {
            return {
              ...prev, replyTo: { id: comment.id, userId: comment.userId, userProfileName: comment.userProfileName, content: comment.content }
            }
          })} /> : ""}
        {(user.role === roles.ADMIN || (user.id && user.id === comment.userId)) && !comment.deletedAt ? <Button text="Delete" onClick={() => {
          const userConfirmed = window.confirm("Are you sure you want to delete this comment?")
          if (userConfirmed) {
            setIsDeleteLoading(true)
          }
        }} /> : ""}
      </div>
      <hr />
    </div>
  )
}
