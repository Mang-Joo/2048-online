import styled from "styled-components"
import "antd/dist/antd.css"
import { Row, Col } from "antd"
import { Level } from "../style/Level"
import { useEffect, useState } from "react"

const Frame = styled(Row)`
  width: 40%;
  text-align: center;
  margin: 0 auto;
`
const Title = styled.h1`
  width: 50%;
  text-align: center;
  margin: 0 auto;
  margin-top: 30px;
  margin-bottom: 30px;
  font-size: 4em;
  padding: 10px;
  color: #00b7c2;
  font-weight: bold;
  border: 5px solid #00b7c2;
  border-radius: 10px;
`
const Board = styled(Row)`
  width: 100%;
  background-color: #d9c6a5;
  padding: 7px;
  border-radius: 5px;
`
const Tile = styled.div`
  background-color: ${props =>
    props.lv != undefined ? Level[`LV${props.lv}`] : Level["LV0"]};
  padding: 5px;
  color: black;
  height: 140px;
  border-radius: 5px;
  margin: 7px;
  font-weight: bold;
  font-size: 3rem;
  line-height: 130px;
`

const Home = () => {
  const [game, setGame] = useState([
    [0, 0, 0, 0],
    [0, 0, 0, 0],
    [0, 0, 0, 0],
    [0, 0, 0, 0]
  ])
  useEffect(() => {
    const temp = [...game]

    for (let i = 0; i < 2; i++) {
      const posX = Math.floor(Math.random() * 3) + 1
      const posY = Math.floor(Math.random() * 3) + 1
      temp[posX][posY] = Math.floor(Math.random() * 2) + 1
    }
    setGame(temp)
  }, [setGame])
  return (
    <>
      <Title>Playing!</Title>
      <Frame>
        <Board>
          {game.map(row =>
            row.map((col, index) => (
              <Col key={index} span={6}>
                <Tile lv={col}>{col != 0 ? Math.pow(2, col) : ""}</Tile>
              </Col>
            ))
          )}
        </Board>
      </Frame>
    </>
  )
}

export default Home
