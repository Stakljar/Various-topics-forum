import { useContext, useEffect, useState } from "react"
import discussionCategories from "../../data/discussionCategories"
import Button from "../../components/Button"
import useErrorMessage from "../../hooks/useErrorMessage"
import InfoMessage from "../../components/InfoMessage"
import useRefreshIntercept from "../../hooks/useRefreshIntercept"
import { useNavigate } from "react-router-dom"
import { AuthContext } from "../../App"

export default function NewDiscussion() {
  const { user } = useContext(AuthContext)
  const interceptedInstance = useRefreshIntercept()
  const [errorMessage, handleError, resetError] = useErrorMessage()
  const navigate = useNavigate()
  const [discussion, setDiscussion] = useState({ title: "", description: "", category: "" })
  const [isLoading, setIsLoading] = useState(false)

  useEffect(() => {
    if (!isLoading) {
      return
    }
    const abortController = new AbortController()
    resetError()
    const addDiscussion = async () => {
      try {
        await interceptedInstance.post("/discussions/add",
          discussion,
          {
            signal: abortController.signal,
            headers: {
              "Authorization": "Bearer " + user.accessToken,
            },
          })
        navigate(-1)
      }
      catch (error) {
        setIsLoading(false)
        handleError(error)
      }
    }
    addDiscussion()
    return () => abortController.abort()
  }, [isLoading])

  return (
    <div id="new-discussion">
      <form onSubmit={(e) => {
        e.preventDefault(); !discussion.title.trim() ? alert("Please input discussion title") :
          !discussion.description.trim() ? alert("Please input discussion description") : setIsLoading(true)
      }}>
        <div>
          <label htmlFor="discussion-title-input"><strong>Title: </strong></label>
          <input id="discussion-title-input" className="default-input" type="text" name="title"
            value={discussion.title} onChange={(e) => setDiscussion((prev) => { return { ...prev, [e.target.name]: e.target.value } })} required />
        </div>
        <div>
          <label htmlFor="discussion-description-input"><strong>Description: </strong></label>
          <textarea id="discussion-description-input" name="description" className="default-input" value={discussion.description}
            onChange={(e) => setDiscussion((prev) => { return { ...prev, [e.target.name]: e.target.value } })} rows="7" cols="40" required></textarea>
        </div>
        <div>
          <label htmlFor="category-search-menu"><strong>Category: </strong></label>
          <select id="discussion-category-search-menu" className="default-input" name="category" value={discussion.category}
            onChange={(e) => setDiscussion((prev) => { return { ...prev, [e.target.name]: e.target.value } })} required>
            <option value={""} disabled>Select category</option>
            {
              discussionCategories.map((category, index) => {
                return <option key={index} value={category.name}>{category.value}</option>
              })
            }
          </select>
        </div>
        {errorMessage && <InfoMessage infoMessage={errorMessage} />}
        <div>
          <Button text="Add" type="submit" />
        </div>
      </form>
    </div>
  )
}
