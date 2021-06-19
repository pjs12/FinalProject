import cv2
import socket
import struct
import pickle

ip = '192.168.0.50' # ip 주소
port = 50001 # port 번호

# 소켓 객체를 생성 및 연결
client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
client_socket.connect((ip, port))
print('연결 성공')

# 카메라 선택
camera = cv2.VideoCapture(0)

# 크기 지정
camera.set(cv2.CAP_PROP_FRAME_WIDTH, 640); # 가로
camera.set(cv2.CAP_PROP_FRAME_HEIGHT, 480); # 세로

# 인코드 파라미터
# jpg의 경우 cv2.IMWRITE_JPEG_QUALITY를 이용하여 이미지의 품질을 설정
encode_param = [int(cv2.IMWRITE_JPEG_QUALITY), 90]

while True:
    ret, frame = camera.read() # 카메라 프레임 읽기
    result, frame = cv2.imencode('.jpg', frame, encode_param) # 프레임 인코딩
    # 직렬화(serialization) : 효율적으로 저장하거나 스트림으로 전송할 때 객체의 데이터를 줄로 세워 저장하는 것
    # binary file : 컴퓨터 저장과 처리 목적을 위해 이진 형식으로 인코딩된 데이터를 포함
    data = pickle.dumps(frame, 0) # 프레임을 직렬화화하여 binary file로 변환
    size = len(data)
    print("Frame Size : ", size) # 프레임 크기 출력

    # 데이터(프레임) 전송
    client_socket.sendall(struct.pack(">L", size) + data)

# 메모리를 해제
camera.release()