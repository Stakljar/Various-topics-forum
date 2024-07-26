import { Link } from "react-router-dom"
import DiscussionItem from "../../components/DiscussionItem"
import discussionCategories from "../../data/discussionCategories"
import Button from "../../components/Button"
import { useContext, useEffect, useRef, useState } from "react"
import useErrorMessage from "../../hooks/useErrorMessage"
import axiosInstance from "../../axios/axiosInstances"
import { AxiosError } from "axios"
import InfoMessage from "../../components/InfoMessage"
import Spinner from "../../components/Spinner"
import { AuthContext } from "../../App"
import { roles } from "../../data/roles"
import useRefreshIntercept from "../../hooks/useRefreshIntercept"

export default function Discussions() {
  const { user } = useContext(AuthContext)
  const interceptedInstance = useRefreshIntercept()
  const [searchParams, setSearchParams] = useState({ title: "", category: "", sort: "" })
  const [errorMessage, handleError, resetError] = useErrorMessage()
  const [isLoading, setIsLoading] = useState(true)
  const [discussions, setDiscussions] = useState([])
  const pageNumberRef = useRef(0)
  const isLastRef = useRef(false)
  const isNewSearchRef = useRef(false)

  useEffect(() => {
    if (!isLoading) {
      return
    }
    const abortController = new AbortController()
    resetError()
    const fetchDiscussions = async () => {
      try {
        const instance = user.role === roles.REGULAR_USER ? interceptedInstance : axiosInstance
        const response = await instance.get(`/discussions` + (user.role === roles.REGULAR_USER ? "/with_user_votes" : ""),
          {
            params: {
              page_number: pageNumberRef.current,
              page_size: 10,
              ...(searchParams.category && { category: searchParams.category }),
              ...(searchParams.title && { search_term: searchParams.title }),
              ...(searchParams.sort && { sort_by: searchParams.sort }),
            },
            signal: abortController.signal,
            headers: {
              "Authorization": user.role === roles.REGULAR_USER ? ("Bearer " + user.accessToken) : "",
            },
          })
        const content = response.data.content
        setIsLoading(false)
        const isNewSearch = isNewSearchRef.current
        setDiscussions((prev) => {
          return isNewSearch ? content : [...prev, ...content]
        })
        isNewSearchRef.current = false
        isLastRef.current = response.data.last
      }
      catch (error) {
        if (error.code === AxiosError.ERR_CANCELED) {
          return
        }
        isNewSearchRef.current = false
        setIsLoading(false)
        handleError(error)
      }
    }
    fetchDiscussions()
    return () => abortController.abort()
  }, [isLoading])

  useEffect(() => {
    function handleScroll() {
      const { scrollTop, clientHeight, scrollHeight } = document.documentElement
      if (scrollTop + clientHeight >= scrollHeight && !isLastRef.current) {
        pageNumberRef.current = pageNumberRef.current + 1
        setIsLoading(true)
      }
    }
    window.addEventListener("scroll", handleScroll)
    return () => {
      window.removeEventListener("scroll", handleScroll)
    }
  }, [])  

  return (
    <div id="discussions">
      <div id="search-and-add-new-discussion">
        <form id="search-form" onSubmit={(e) => { e.preventDefault(); isNewSearchRef.current = true; pageNumberRef.current = 0; setIsLoading(true) }}>
          <div id="search-form-search-term" >
            <input className="default-input" type="search" name="title" value={searchParams.title}
              onChange={(e) => setSearchParams((prev) => { return { ...prev, [e.target.name]: e.target.value } })} placeholder="Input interests..." />
            <Button text="Search" type="submit" />
          </div>
          <div>
            <div>
              <label htmlFor="category-search-menu">Category: </label>
            </div>
            <select id="category-search-menu" className="default-input" name="category" value={searchParams.category}
              onChange={(e) => setSearchParams((prev) => { return { ...prev, [e.target.name]: e.target.value } })}>
              <option value="">All</option>
              {
                discussionCategories.map((category, index) => {
                  return <option key={index} value={category.name}>{category.value}</option>
                })
              }
            </select>
          </div>
          <div>
            <div>
              <label htmlFor="sort-search-menu">Sort by: </label>
            </div>
            <select id="sort-search-menu" className="default-input" value={searchParams.sort}
              onChange={(e) => setSearchParams((prev) => { return { ...prev, sort: e.target.value } })}>
              <option value="popularity">Popularity</option>
              <option value="date">Date</option>
            </select>
          </div>
        </form>
        {
          user.role === roles.REGULAR_USER &&
          <div id="add-new-discussion">
            <Link to="/new-discussion"><Button text="Add new discussion" /></Link>
          </div>
        }
      </div>
      <div id="discussion-items">
        {errorMessage && <InfoMessage infoMessage={errorMessage} />}
        {
          discussions.map((discussion) => {
            return <DiscussionItem key={discussion.id} discussion={discussion} setDiscussions={setDiscussions} interceptedInstance={interceptedInstance} />
          })
        }
        {isLoading && <Spinner />}
      </div>
    </div>
  )
}
