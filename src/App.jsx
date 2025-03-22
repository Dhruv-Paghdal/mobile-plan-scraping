import React from 'react'
import Navbar from './Components/Navbar/Navbar'
import SearchFilter from './Components/Search/SearchFilter'
import Card from './Components/Cards/Card'
const App=()=>{
  return (
    <div>
      <Navbar />
      <SearchFilter />
      <Card  />
      </div>
  )
}
export default App