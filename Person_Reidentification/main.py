import socket
import cv2
import pickle
import struct 

import cv2
from libs.camera import VideoCamera
from logging import getLogger, basicConfig, DEBUG, INFO
import os
import sys
import cv2
import numpy as np
import json
import base64
from libs.interactive_detection import Detections, Tracker
from libs.argparser import build_argparser
from libs.publishing2 import AWSClient
from datetime import datetime
from openvino.inference_engine import get_version
import configparser

# awsclient = AWSClient()

devices = ["CPU", "CPU"]
is_async = True
is_det = False
is_reid = True

frame = np.full((640,480,3), (0,0,0), np.uint8)
detections = Detections(frame, devices, 0, 10)

ip = '192.168.0.27' # ip 주소
port = 50001 # port 번호

s = socket.socket(socket.AF_INET,socket.SOCK_STREAM) # 소켓 객체를 생성
s.bind((ip, port)) # 바인드(bind) : 소켓에 주소, 프로토콜, 포트를 할당
s.listen(10) # 연결 수신 대기 상태(리스닝 수(동시 접속) 설정)
print('클라이언트 연결 대기')

# 연결 수락(클라이언트 소켓 주소를 반환)
conn, addr = s.accept()
print(addr) # 클라이언트 주소 출력

data = b"" # 수신한 데이터를 넣을 변수
payload_size = struct.calcsize(">L")

while True:
    # 프레임 수신
    while len(data) < payload_size:
        data += conn.recv(4096)
    packed_msg_size = data[:payload_size]
    data = data[payload_size:]
    msg_size = struct.unpack(">L", packed_msg_size)[0]
    while len(data) < msg_size:
        data += conn.recv(4096)
    frame_data = data[:msg_size]
    data = data[msg_size:]
    print("Frame Size : {}".format(msg_size)) # 프레임 크기 출력

    # 역직렬화(de-serialization) : 직렬화된 파일이나 바이트를 원래의 객체로 복원하는 것
    frame=pickle.loads(frame_data, fix_imports=True, encoding="bytes") # 직렬화되어 있는 binary file로 부터 객체로 역직렬화
    frame = cv2.imdecode(frame, cv2.IMREAD_COLOR) # 프레임 디코딩

    #re-id
    frame = detections.person_detection(frame, is_async, is_det, is_reid)
    # 영상 출력
    cv2.imshow('TCP_Frame_Socket',frame)
    
    #키 입력 상태를 받음
    if cv2.waitKey(1) == ord('q') : # q를 입력하면 종료
        break
