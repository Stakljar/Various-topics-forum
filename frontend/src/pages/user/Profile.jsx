import { useContext, useEffect, useRef, useState } from "react"
import { useParams } from "react-router-dom"
import ListDiscussionItem from "../../components/ListDiscussionItem"
import ListCommentItem from "../../components/ListCommentItem"
import Button from "../../components/Button"
import useErrorMessage from "../../hooks/useErrorMessage"
import axiosInstance from "../../axios/axiosInstances"
import { AxiosError, HttpStatusCode } from "axios"
import InfoMessage from "../../components/InfoMessage"
import { AuthContext } from "../../App"
import Spinner from "../../components/Spinner"
import useRefreshIntercept from "../../hooks/useRefreshIntercept"
import useErrorAlert from "../../hooks/useErrorAlert"
import { roles } from "../../data/roles"

export default function Profile() {
  const { userId } = useParams()
  const { user } = useContext(AuthContext)
  const errorAlert = useErrorAlert()
  const [userErrorMessage, handleUserError, resetUserError, setUserErrorMessage] = useErrorMessage()
  const interceptedInstance = useRefreshIntercept()
  const [isUserLoading, setIsUserLoading] = useState(true)
  const [activeTab, setActiveTab] = useState("discussions")
  const [userData, setUserData] = useState({})
  const [comments, setComments] = useState([])
  const [discussions, setDiscussions] = useState([])
  const [areDiscussionsLoading, setAreDiscussionsLoading] = useState(false)
  const [areCommentsLoading, setAreCommentsLoading] = useState(false)
  const discussionPageNumberRef = useRef(0)
  const isDiscussionLastRef = useRef(false)
  const commentPageNumberRef = useRef(0)
  const isCommentLastRef = useRef(false)
  const didDiscussionsLoadRef = useRef(false)
  const didCommentsLoadRef = useRef(false)

  useEffect(() => {
    if (!isUserLoading) {
      return
    }
    const abortController = new AbortController()
    resetUserError()
    const fetchUser = async () => {
      try {
        const response = await axiosInstance.get("/users/" + userId, {
          signal: abortController.signal,
        })
        setIsUserLoading(false)
        setUserData(response.data)
      }
      catch (error) {
        if (error.code === AxiosError.ERR_CANCELED) {
          return
        }
        setIsUserLoading(false)
        if (error.response?.status === HttpStatusCode.BadRequest) {
          setUserErrorMessage("Invalid id supplied")
        }
        else {
          handleUserError(error)
        }
      }
    }
    fetchUser()
    return () => abortController.abort()
  }, [isUserLoading])

  useEffect(() => {
    if (!areDiscussionsLoading) {
      return
    }
    const abortController = new AbortController()
    const fetchDiscussions = async () => {
      try {
        const response = await interceptedInstance.get("/discussions/user", {
          params: {
            page_number: discussionPageNumberRef.current,
            page_size: 15,
          },
          signal: abortController.signal,
          headers: {
            "Authorization": "Bearer " + user.accessToken,
          },
        })
        setAreDiscussionsLoading(false)
        isDiscussionLastRef.current = response.data.last
        setDiscussions((prev) => { return [...prev, ...response.data.content] })
      }
      catch (error) {
        setAreDiscussionsLoading(false)
        errorAlert(error)
      }
    }
    fetchDiscussions()
    return () => abortController.abort()
  }, [areDiscussionsLoading])

  useEffect(() => {
    if (!areCommentsLoading) {
      return
    }
    const abortController = new AbortController()
    const fetchComments = async () => {
      try {
        const response = await interceptedInstance.get("/comments/user", {
          params: {
            page_number: commentPageNumberRef.current,
            page_size: 15,
          },
          signal: abortController.signal,
          headers: {
            "Authorization": "Bearer " + user.accessToken,
          },
        })
        setAreCommentsLoading(false)
        isCommentLastRef.current = response.data.last
        setComments((prev) => { return [...prev, ...response.data.content] })
      }
      catch (error) {
        setAreCommentsLoading(false)
        errorAlert(error)
      }
    }
    fetchComments()
    return () => abortController.abort()
  }, [areCommentsLoading])

  return (
    <div id="profile">
      {
        userErrorMessage ? <InfoMessage infoMessage={userErrorMessage} /> : isUserLoading ? <Spinner /> :
          <>
            <div id="profile-info">
              <h2>{userData.profileName}</h2>
              <div><strong>Date created: </strong>{userData.createdAt}</div>
              <div><strong>Total discussions started: </strong>{userData.discussionsCount}</div>
              <div><strong>Total comments: </strong>{userData.commentsCount}</div>
            </div>
            {
              (user.id === userData.id && user.role === roles.REGULAR_USER) &&
              <>
                <div id="profile-tabs">
                  <div className="tabs">
                    <button className={`tab ${activeTab === "discussions" ? "active" : ""}`}
                      onClick={() => setActiveTab("discussions")}>
                      Discussions
                    </button>
                    <button className={`tab ${activeTab === "comments" ? "active" : ""}`}
                      onClick={() => setActiveTab("comments")}>
                      Comments
                    </button>
                  </div>
                  <div className="tab-content">
                    {
                      activeTab === "discussions" &&
                      <>
                        <div>
                          {
                            discussions.map((discussion) =>
                              <ListDiscussionItem key={discussion.id} discussion={discussion} setDiscussions={setDiscussions} interceptedInstance={interceptedInstance} />
                            )
                          }
                          {areDiscussionsLoading && <Spinner />}
                        </div>
                        {
                          didDiscussionsLoadRef.current ?
                            (isDiscussionLastRef.current || <Button text="Load more"
                              onClick={() => { discussionPageNumberRef.current = discussionPageNumberRef.current + 1; setAreDiscussionsLoading(true) }} />) :
                            <Button text="Load discussions" onClick={() => { didDiscussionsLoadRef.current = true; setAreDiscussionsLoading(true) }} />
                        }
                      </>
                    }
                    {
                      activeTab === "comments" &&
                      <>
                        <div>
                          {
                            comments.map((comment) =>
                              <ListCommentItem key={comment.id} comment={comment} setComments={setComments} interceptedInstance={interceptedInstance} />
                            )
                          }
                          {areCommentsLoading && <Spinner />}
                        </div>
                        {
                          didCommentsLoadRef.current ?
                            (isCommentLastRef.current || <Button text="Load more"
                              onClick={() => { commentPageNumberRef.current = commentPageNumberRef.current + 1; setAreCommentsLoading(true) }} />) :
                            <Button text="Load comments" onClick={() => { didCommentsLoadRef.current = true; setAreCommentsLoading(true) }} />
                        }
                      </>
                    }
                  </div>
                </div>
              </>
            }
          </>
      }
    </div>
  )
}
