export default function EmailAndPasswordInput({ credentials, setCredentials, type }) {
  return (
    <>
      <div>
        <label htmlFor="email-input"><strong>Email: </strong></label>
        <input id="email-input" className="default-input" type="email" name="email"
          value={credentials.email} onChange={(e) => setCredentials((prev) => { return { ...prev, [e.target.name]: e.target.value } })} required />
      </div>
      <div>
        <label htmlFor="password-input"><strong>Password: </strong></label>
        {
          type === "signup" ?
            <input id="password-input" className="default-input" type="password" name="password" value={credentials.password}
              onChange={(e) => setCredentials((prev) => { return { ...prev, [e.target.name]: e.target.value } })} minLength="8" /> :
            <input id="password-input" className="default-input" type="password" name="password" value={credentials.password}
              onChange={(e) => setCredentials((prev) => { return { ...prev, [e.target.name]: e.target.value } })} required />
        }
      </div>
    </>
  )
}
