import Layout from "../components/Layout"
import GameBoard from "../components/GameBoard"
import React, { useEffect, useState } from "react"
import styled from "styled-components"
import { Button, Col, message, Row, Typography } from "antd"
import { useRouter } from "next/router"
import hotkeys from "hotkeys-js"
import { exitRoom } from "../apis/room"
import stompClient from "../util/socket-util"
import Timer from "../components/Timer"
import { Modal } from "antd"
import Header from "../components/Header"
import GameBoard2 from "../components/GameBoard2"
import { getUsername } from "../util/jwt-util"

function info(players: Player[]) {
  if (!players) return
  Modal.info({
    title: <Title level={3}>결과</Title>,
    content: (
      <>
        {players &&
          players.map(player => (
            <div>
              <h3>{player.nickname}</h3>
              <span>{player.gameInfo.score}</span>
            </div>
          ))}
      </>
    ),
    onOk() {}
  })
}

const { Title } = Typography

const MyTitle = styled(Title)`
  color: ${props => (props["data-is-host"] ? "yellow !important" : "")};
`
const Frame = styled.div`
  float: left;
  background-color: #ceefff;
  border-radius: 10px;
  margin: 4px;
`
const Head = styled.div`
  background-color: #ff8585;
  color: white;
`
const Info = styled.div`
  text-align: center;
  padding-top: 10px;

  h3 {
    color: white;
    text-shadow: -2px 0 black, 0 2px black, 2px 0 black, 0 -2px black;
  }
`
const ScoreBox = styled.div`
  text-align: center;
  color: white;
  h2 {
    font-weight: bold;
    color: white;
  }
  h3 {
    color: white;
  }
`
const DummyBoard = styled.div`
  width: 270px;
  height: 270px;
`
const key = "updatable"

type GameInfo = {
  board: number[][]
  score: number
  gameOver: boolean
}
type Player = {
  nickname: string
  gameInfo: GameInfo | null
}
type GameRoom = {
  name: string
  players: Array<Player>
  gameMode: string
  maxNumberOfPeople: string
  start: boolean
  host: string
}

const Room = () => {
  const router = useRouter()
  const [gameInfo, setGameInfo] = useState<GameRoom>({
    name: "",
    players: [],
    gameMode: "",
    maxNumberOfPeople: "",
    start: false,
    host: ""
  })
  const [startDate, setStartDate] = useState<Date>(null)
  useEffect(() => {
    if (!gameInfo.start) return

    hotkeys("left", e => {
      e.preventDefault()
      if (gameInfo.start) {
        stompClient.send("/pub/multi/left", {})
      }
    })
    hotkeys("right", e => {
      e.preventDefault()
      if (gameInfo.start) {
        stompClient.send("/pub/multi/right", {})
      }
    })
    hotkeys("up", e => {
      e.preventDefault()
      if (gameInfo.start) {
        stompClient.send("/pub/multi/top", {})
      }
    })
    hotkeys("down", e => {
      e.preventDefault()
      if (gameInfo.start) {
        stompClient.send("/pub/multi/bottom", {})
      }
    })

    return () => {
      hotkeys.unbind("left")
      hotkeys.unbind("right")
      hotkeys.unbind("up")
      hotkeys.unbind("down")
    }
  }, [gameInfo.start])

  useEffect(() => {
    if (!router?.query) return

    const { roomId } = router.query
    const headers = {
      Authorization: localStorage.getItem("token")
    }

    stompClient.connect(headers, () => {
      stompClient.send("/pub/multi/init", {}, roomId)
      stompClient.subscribe(`/sub/room/${roomId}`, response => {
        const payload: GameRoom = JSON.parse(response.body)

        setGameInfo(payload)
      })
      stompClient.subscribe(`/sub/room/${roomId}/result`, response => {
        const payload: Player[] = JSON.parse(response.body)
        info(payload)
        setStartDate(null)
      })
      stompClient.subscribe(`/sub/room/${roomId}/start`, response => {
        const payload: string = JSON.parse(response.body)
        setStartDate(new Date(payload))
      })
    })
  }, [router])

  const handleExit = async () => {
    await exitRoom()
    message.success({ content: "방에서 나왔습니다!", key, duration: 2 })
    router.push({
      pathname: "/rooms"
    })
  }
  const handleStart = () => {
    if (!gameInfo.start) {
      stompClient.send("/pub/multi/start", {})
    }
  }
  return (
    <div>
      <Header />
      <Timer startDate={startDate} />
      <Row justify="center">
        <Col span={11} xs={14}>
          <div>
            <Button danger onClick={handleExit}>
              방 나가기
            </Button>
            {!gameInfo.start && (
              <Button type="primary" onClick={handleStart}>
                게임 시작
              </Button>
            )}
          </div>
          {gameInfo.players
            .filter(p => p.nickname === getUsername())
            .map(player => (
              <Frame>
                <Head>
                  <Info>
                    <MyTitle
                      level={3}
                      data-is-host={gameInfo.host === player.nickname}
                    >
                      {player.nickname}
                    </MyTitle>
                  </Info>
                  {player.gameInfo && (
                    <ScoreBox>
                      <h3>SCORE {player.gameInfo.score}</h3>
                    </ScoreBox>
                  )}
                </Head>
                {!player.gameInfo && <DummyBoard></DummyBoard>}
                {player.gameInfo && (
                  <GameBoard
                    board={player.gameInfo.board}
                    over={player.gameInfo.gameOver}
                  />
                )}
              </Frame>
            ))}

          <Frame>
            {gameInfo.players
              .filter(p => p.nickname !== getUsername())
              .map(player => (
                <>
                  <Head>
                    <Info>
                      <MyTitle level={4}>
                        {player.nickname} : {player.gameInfo.score}
                      </MyTitle>
                    </Info>
                  </Head>
                  {player.gameInfo && (
                    <GameBoard2
                      board={player.gameInfo.board}
                      over={player.gameInfo.gameOver}
                    />
                  )}
                </>
              ))}
          </Frame>
        </Col>
      </Row>
    </div>
  )
}

export default Room
