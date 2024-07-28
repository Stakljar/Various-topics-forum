import { useNavigate, useParams } from "react-router-dom"
import { roles } from "../../data/roles"
import { useContext, useEffect, useRef, useState } from "react"
import { AuthContext } from "../../App"
import Comment from "../../components/Comment"
import Button from "../../components/Button"
import Voting from "../../components/Voting"
import useErrorMessage from "../../hooks/useErrorMessage"
import axiosInstance from "../../axios/axiosInstances"
import Spinner from "../../components/Spinner"
import InfoMessage from "../../components/InfoMessage"
import { AxiosError, HttpStatusCode } from "axios"
import useRefreshIntercept from "../../hooks/useRefreshIntercept"
import CommentReply from "../../components/CommentReply"
import useErrorAlert from "../../hooks/useErrorAlert"
import discussionCategories from "../../data/discussionCategories"

export default function Discussion() {
  const { user } = useContext(AuthContext)
  const { discussionId } = useParams()
  const errorAlert = useErrorAlert()
  const navigate = useNavigate()
  const interceptedInstance = useRefreshIntercept()
  const [discussionErrorMessage, handleDiscussionError, resetDiscussionError, setDiscussionErrorMessage] = useErrorMessage()
  const [commentsErrorMessage, handleCommentsError, resetCommentsError, setCommentsErrorMessage] = useErrorMessage()
  const [isDiscussionDeleteLoading, setIsDiscussionDeleteLoading] = useState(false)
  const [isDiscussionLoading, setIsDiscussionLoading] = useState(true)
  const [areCommentsLoading, setAreCommentsLoading] = useState(true)
  const [isCommentAddLoading, setIsCommentAddLoading] = useState(false)
  const [isDiscussionVoteLoading, setIsDiscussionVoteLoading] = useState(false)
  const [discussion, setDiscussion] = useState({})
  const [comments, setComments] = useState([])
  const [comment, setComment] = useState({ discussionId: discussionId, content: "", replyTo: { id: null, userId: null, userProfileName: null, content: "" } })
  const isDiscussionUpvoteRef = useRef(null)
  const isLastRef = useRef(false)
  const pageNumberRef = useRef(0)
  const totalPagesRef = useRef(0)

  useEffect(() => {
    if (!isDiscussionLoading) {
      return
    }
    const abortController = new AbortController()
    resetDiscussionError()
    const fetchDiscussion = async () => {
      try {
        const instance = user.role === roles.REGULAR_USER ? interceptedInstance : axiosInstance
        const response = await instance.get("/discussions" + (user.role === roles.REGULAR_USER ? "/with_user_votes/" : "/") + discussionId, {
          params: { discussionId },
          signal: abortController.signal,
          headers: {
            "Authorization": user.role === roles.REGULAR_USER ? ("Bearer " + user.accessToken) : "",
          },
        })
        setIsDiscussionLoading(false)
        isDiscussionUpvoteRef.current = response.data.isUserUpvote
        setDiscussion(response.data)
      }
      catch (error) {
        if (error.code === AxiosError.ERR_CANCELED) {
          return
        }
        setIsDiscussionLoading(false)
        if (error.response?.status === HttpStatusCode.BadRequest) {
          setDiscussionErrorMessage("Invalid id supplied")
        }
        else {
          handleDiscussionError(error)
        }
      }
    }
    fetchDiscussion()
    return () => abortController.abort()
  }, [isDiscussionLoading])

  useEffect(() => {
    if (!areCommentsLoading) {
      return
    }
    const abortController = new AbortController()
    resetCommentsError()
    const fetchComments = async () => {
      try {
        const instance = user.role === roles.REGULAR_USER ? interceptedInstance : axiosInstance
        const response = await instance.get("/comments" + (user.role === roles.REGULAR_USER ? "/with_user_votes" : ""), {
          params: {
            page_number: pageNumberRef.current,
            page_size: 8,
            discussion_id: discussionId,
          },
          signal: abortController.signal,
          headers: {
            "Authorization": user.role === roles.REGULAR_USER ? ("Bearer " + user.accessToken) : "",
          },
        })
        totalPagesRef.current = response.data.totalPages
        isLastRef.current = response.data.last
        setAreCommentsLoading(false)
        setComments(response.data.content)
      }
      catch (error) {
        if (error.code === AxiosError.ERR_CANCELED) {
          return
        }
        setAreCommentsLoading(false)
        if (error.response?.status === HttpStatusCode.BadRequest) {
          setCommentsErrorMessage("Invalid id supplied")
        }
        else {
          handleCommentsError(error)
        }
      }
    }
    fetchComments()
    return () => abortController.abort()
  }, [areCommentsLoading])

  useEffect(() => {
    if (!isDiscussionVoteLoading) {
      return
    }
    const abortController = new AbortController()
    const vote = async () => {
      try {
        const response = await interceptedInstance.post(`/discussions/vote`,
          { discussionId: discussionId, isUpvote: isDiscussionUpvoteRef.current },
          {
            headers: {
              "Authorization": "Bearer " + user.accessToken,
            },
            signal: abortController.signal,
          })
        setIsDiscussionVoteLoading(false)
        const isUpvote = response.data.isUpvote
        setDiscussion((prev) => {
          return {
            ...prev, upvoteCount: isUpvote && !prev.isUserUpvote ? prev.upvoteCount + 1 :
              !isUpvote && prev.isUserUpvote ? prev.upvoteCount - 1 : prev.upvoteCount,
            downvoteCount: isUpvote === false && (prev.isUserUpvote || prev.isUserUpvote === null) ? prev.downvoteCount + 1 :
              (isUpvote || isUpvote === null) && prev.isUserUpvote === false ? prev.downvoteCount - 1 : prev.downvoteCount,
            isUserUpvote: isUpvote
          }
        })
      }
      catch (error) {
        setIsDiscussionVoteLoading(false)
        errorAlert(error)
      }
    }
    vote()
    return () => abortController.abort()
  }, [isDiscussionVoteLoading])

  useEffect(() => {
    if (!isCommentAddLoading) {
      return
    }
    const abortController = new AbortController()
    const addComment = async () => {
      try {
        const response = await interceptedInstance.post(`/comments/add`,
          { discussionId: comment.discussionId, parentCommentId: comment.replyTo.id, content: comment.content },
          {
            signal: abortController.signal,
            headers: {
              "Authorization": "Bearer " + user.accessToken,
            },
          })
        setIsCommentAddLoading(false)
        setComment({ discussionId: discussionId, content: "", replyTo: { id: null, userId: null, userProfileName: "", content: "" } })
        if(totalPagesRef.current - 1 === pageNumberRef.current || totalPagesRef.current === 0) {
          setComments((prev) => {
            return [...prev, response.data]
          })
        }
      }
      catch (error) {
        setIsCommentAddLoading(false)
        if (error.response?.status === HttpStatusCode.BadRequest) {
          alert("Invalid id supplied")
        }
        else {
          errorAlert(error)
        }
      }
    }
    addComment()
    return () => abortController.abort()
  }, [isCommentAddLoading])

  useEffect(() => {
    if (!isDiscussionDeleteLoading) {
      return
    }
    const abortController = new AbortController()
    const deleteDiscussion = async () => {
      try {
        await interceptedInstance.delete(`/discussions/remove/${discussionId}`, {
          signal: abortController.signal,
          headers: {
            "Authorization": user.accessToken,
          },
        })
        navigate(-1)
      }
      catch (error) {
        setIsDiscussionDeleteLoading(false)
        errorAlert(error)
      }
    }
    deleteDiscussion()
    return () => abortController.abort()
  }, [isDiscussionDeleteLoading])

  return (
    <div id="discussion">
      <div>
        {
          isDiscussionLoading ? <Spinner /> :
            discussionErrorMessage ? <InfoMessage infoMessage={discussionErrorMessage} /> :
              <>
                <div id="discussion-info-and-delete">
                  <div id="discussion-info">Posted on: {discussion.createdAt} by <span onClick={() => discussion.userId && navigate(`/profile/${discussion.userId}`)}>
                    {discussion.userProfileName || "Deleted user"}</span>
                  </div>
                  {(user.role === roles.ADMIN || (user.id && user.id === discussion.userId)) && <Button text="Delete" onClick={() => {
                    const userConfirmed = window.confirm("Are you sure you want to delete this discussion?")
                    if (userConfirmed) {
                      setIsDiscussionDeleteLoading(true)
                    }
                  }} />}
                </div>
                <div id="discussion-category">Category: {discussionCategories.find(d => d.name === discussion.category)?.value}</div>
                <h2 id="discussion-title">{discussion.title}</h2>
                <div id="discussion-description">{discussion.description}</div>
                <Voting handleClick={(isUpvote) => {
                  if (user.role !== roles.REGULAR_USER) { alert("You need to be signed in to vote.") }
                  else { isDiscussionUpvoteRef.current = isUpvote; setIsDiscussionVoteLoading(true) }
                }} upvotes={discussion.upvoteCount} downvotes={discussion.downvoteCount} isUpvote={discussion.isUserUpvote} />
              </>
        }
      </div>
      <hr />
      {user.role === roles.REGULAR_USER &&
        <form id="add-comment" onSubmit={(e) => { e.preventDefault(); setIsCommentAddLoading(true) }}>
          {
            comment.replyTo.id !== null &&
            <CommentReply userId={comment.replyTo.userId} userProfileName={comment.replyTo.userProfileName} content={comment.replyTo.content}
              cancelReply={() => setComment((prev) => { return { ...prev, replyTo: { id: null, userId: null, userProfileName: null, content: "" } } })} />
          }
          <input type="text" className="default-input" value={comment.content} name="content"
            onChange={(e) => setComment((prev) => { return { ...prev, [e.target.name]: e.target.value } })} placeholder="Write comment" required />
          <Button text="Add" type="submit" />
        </form>
      }
      {commentsErrorMessage && <InfoMessage infoMessage={commentsErrorMessage} />}
      <div id="discussion-comments">
        {
          comments.map((comment) => {
            return <Comment key={comment.id} comment={comment} setComments={setComments} setComment={setComment} interceptedInstance={interceptedInstance} />
          })
        }
      </div>
      {areCommentsLoading && <Spinner />}
      <div id="comment-pages-selection">
        {
          Array.from(Array(totalPagesRef.current).keys()).map((page) =>
            <div key={page} className={page === pageNumberRef.current ? "current" : ""}
              onClick={() => { pageNumberRef.current = page; setAreCommentsLoading(true) }}>
              <strong>{page + 1}</strong>
            </div>)
        }
      </div>
    </div>
  )
}
