import { useContext, useEffect, useRef, useState } from "react"
import { AuthContext } from "../App"
import { roles } from "../data/roles"
import { useNavigate } from "react-router-dom"
import Voting from "./Voting"
import Button from "./Button"
import useErrorAlert from "../hooks/useErrorAlert"

export default function DiscussionItem({ discussion, setDiscussions, interceptedInstance }) {
  const { user } = useContext(AuthContext)
  const navigate = useNavigate()
  const isUpvoteRef = useRef(null)
  const errorAlert = useErrorAlert()
  const [isVoteLoading, setIsVoteLoading] = useState(false)
  const [isDeleteLoading, setIsDeleteLoading] = useState(false)
  const [votes, setVotes] = useState({ upvotes: discussion.upvoteCount, downvotes: discussion.downvoteCount, isUpvote: discussion.isUserUpvote })

  useEffect(() => {
    if (!isVoteLoading) {
      return
    }
    const abortController = new AbortController()
    const vote = async () => {
      try {
        const response = await interceptedInstance.post(`/discussions/vote`,
          { discussionId: discussion.id, isUpvote: isUpvoteRef.current },
          {
            headers: {
              "Authorization": "Bearer " + user.accessToken,
            },
            signal: abortController.signal,
          })
        setIsVoteLoading(false)
        const isUpvote = response.data.isUpvote
        setVotes((prev) => {
          return {
            upvotes: isUpvote && !prev.isUpvote ? prev.upvotes + 1 :
              !isUpvote && prev.isUpvote ? prev.upvotes - 1 : prev.upvotes,
            downvotes: isUpvote === false && (prev.isUpvote || prev.isUpvote === null) ? prev.downvotes + 1 :
              (isUpvote || isUpvote === null) && prev.isUpvote === false ? prev.downvotes - 1 : prev.downvotes,
            isUpvote: isUpvote
          }
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
    <div className="discussion-item">
      <div className="discussion-title-date-and-delete">
        <h3 onClick={() => navigate("/discussions/" + discussion.id,
          { state: { ...discussion, upvoteCount: votes.upvotes, downvoteCount: votes.downvotes, isUserUpvote: votes.isUpvote } })}>
          {discussion.title.length > 150 ? discussion.title.substring(0, 150) + "..." : discussion.title}
        </h3>
        <div>
          <div className="discussion-date">Posted on: {discussion.createdAt}</div>
          {
            (user.role === roles.ADMIN || (user.id && user.id === discussion.userId)) &&
            <div className="discussion-delete">
              <Button text="Delete" onClick={() => {
                const userConfirmed = window.confirm("Are you sure you want to delete this discussion?")
                if (userConfirmed) {
                  setIsDeleteLoading(true)
                }
              }} />
            </div>
          }
        </div>
      </div>
      <div>
        <div>
          {discussion.description.length > 450 ? discussion.description.substring(0, 450) + "..." : discussion.description}
        </div>
        <div className="discussion-voting" >
          <Voting handleClick={(isUpvote) => {
            if (user.role !== roles.REGULAR_USER) { alert("You need to be signed in to vote.") }
            else { isUpvoteRef.current = isUpvote; setIsVoteLoading(true) }
          }} upvotes={votes.upvotes} downvotes={votes.downvotes} isUpvote={votes.isUpvote} />
        </div>
      </div>
    </div>
  )
}
