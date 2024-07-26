import { useContext, useEffect, useState } from "react"
import Button from "./Button"
import { AuthContext } from "../App"
import { useNavigate } from "react-router-dom"
import useErrorAlert from "../hooks/useErrorAlert"

export default function ListCommentItem({ comment, setComments, interceptedInstance }) {
  const { user } = useContext(AuthContext)
  const navigate = useNavigate()
  const errorAlert = useErrorAlert()
  const [isDeleteLoading, setIsDeleteLoading] = useState(false)

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
    <div className="profile-list-item">
      <div>
        <div>
          <span>Date: {comment.createdAt}</span>
        </div>
        <div className="comment-content" onClick={() => navigate("/discussions/" + comment.discussionId)}>{comment.content}</div>
      </div>
      <Button text="Delete" onClick={() => setIsDeleteLoading(true)} />
    </div>
  )
}
