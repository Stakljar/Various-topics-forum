import { useNavigate } from "react-router-dom"
import Button from "./Button"
import { useContext, useEffect, useState } from "react"
import { AuthContext } from "../App"
import useErrorAlert from "../hooks/useErrorAlert"

export default function ListDiscussionItem({ discussion, setDiscussions, interceptedInstance }) {
  const { user } = useContext(AuthContext)
  const navigate = useNavigate()
  const errorAlert = useErrorAlert()
  const [isDeleteLoading, setIsDeleteLoading] = useState(false)

  useEffect(() => {
    if (!isDeleteLoading) {
      return
    }
    const abortController = new AbortController()
    const deleteDiscussion = async () => {
      try {
        await interceptedInstance.delete(`/discussions/remove/${discussion.id}`, {
          signal: abortController.signal,
          headers: {
            "Authorization": user.accessToken,
          },
        })
        setDiscussions((prev) => prev.filter(d => d.id !== discussion.id))
      }
      catch (error) {
        setIsDeleteLoading(false)
        errorAlert(error)
      }
    }
    deleteDiscussion()
    return () => abortController.abort()
  }, [isDeleteLoading])

  return (
    <div className="profile-list-item">
      <div>
        <div>
          <span>Date: {discussion.createdAt}</span>
        </div>
        <div>
          <strong className="discussion-title" onClick={() => navigate("/discussions/" + discussion.id)}>{discussion.title}</strong>
        </div>
      </div>
      <Button text="Delete" onClick={() => setIsDeleteLoading(true)} />
    </div>
  )
}
