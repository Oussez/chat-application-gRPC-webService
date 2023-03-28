import grpc

import chatSystem_pb2 as chatSys_pb2
import chatSystem_pb2_grpc as chatSys_pb2_grpc

# Adresse et port du serveur GRPC
SERVER_ADDRESS = 'localhost:9999'

def run():
    with grpc.insecure_channel(SERVER_ADDRESS) as channel:
        # Instancier le stub de service GRPC
        stub = chatSys_pb2_grpc.ChatServiceStub(channel)

        # Créer une instance du message de demande
        request = chatSys_pb2.requestMsg(userName='Alice', request='Hello!')

        # Appeler la méthode de service avec bi-directional streaming
        response_stream = stub.chatStream(iter([request]))

        # Parcourir la réponse du streaming
        for response in response_stream:
            print(response.responseToRequest)

if __name__ == '__main__':
    run()