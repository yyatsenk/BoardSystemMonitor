
#include <unistd.h> 
#include <stdio.h> 
#include <sys/types.h>
#include <sys/socket.h> 
#include <stdlib.h> 
#include <netinet/in.h> 
#include <netinet/tcp.h>
#include <sys/stat.h>
#include <string.h> 
#include <iostream>
#include <fstream>
#include <unistd.h>
#include <fcntl.h>
#include <thread>

#define PORT 5004

class MessagingVCU {
    int server_fd = -1;
    int new_socket;
    int valread;
    int opt = 1;
    int addrlen = sizeof(address); 
    char buffer[1024] = {0}; 
    struct sockaddr_in address;
    
    MessagingVCU() {
        if ((server_fd = socket(AF_INET, SOCK_STREAM, 0)) == 0) 
            perror("socket failed"); 
    }
    public:
    /**
     *  Open port for listening
     **/
    int Open() {
        if (server_fd < 0)
            return EXIT_FAILURE;
        if (setsockopt(server_fd, IPPROTO_TCP, TCP_NODELAY, 
                                                  &opt, sizeof(opt))) 
        { 
            perror("setsockopt");
            exit(EXIT_FAILURE); 
        } 
        address.sin_family = AF_INET; 
        address.sin_addr.s_addr = ((in_addr_t) 0x00000000); 
        address.sin_port = htons( PORT ); 
       
        if (bind(server_fd, (struct sockaddr *)&address,  
                                 sizeof(address))<0) 
        { 
            perror("bind failed");
            exit(EXIT_FAILURE); 
        }
        if (listen(server_fd, 3) < 0) 
        { 
            perror("listen failed");
            exit(EXIT_FAILURE); 
        } 
        return EXIT_SUCCESS;
    }
    /**
    *  Accept incomming messages
    **/
    void WaitForReceive(char *recieved) {
        
        if ((new_socket = accept(server_fd, (struct sockaddr *)&address,  
                       (socklen_t*)&addrlen))<0) 
        { 
            perror("accept failed");
            exit(EXIT_FAILURE); 
        }
        recv(new_socket, recieved, 1024,0);
    }
    /**
    *  Send data to client
    **/
    int Send(void *data) {
        if (server_fd < 0)
            return EXIT_FAILURE;
        send(new_socket , static_cast<char*>(data) , strlen(static_cast<char*>(data)) , 0);
        close(new_socket);
        return EXIT_SUCCESS;
    }
    ~MessagingVCU() {
        close(server_fd);
    }
    /**
    *  Creates MessagingVCU instance
    **/
    static MessagingVCU  &getMessagingVCU() {
        static MessagingVCU server;
        return server;
    }
};

int main(int argc, char const *argv[]) 
{ 
    char recieved[1024] = {0};
    MessagingVCU *server = &(MessagingVCU::getMessagingVCU());

    if (!server->Open()) {
        while (true) {
            server->WaitForReceive(recieved);
            printf("Recieved Message: %s", recieved);
            if (!strncmp(recieved, "GET_SYSTEM_INFO", 15)) {

                std::thread top_command([]() -> void {
                    system("top -i 1 | grep -E -- 'Memory|CPU' > top.txt");
                });
                if (top_command.joinable())
                    top_command.join();

                char arr[202];
                int fd = open("top.txt", O_RDONLY);

                read(fd, arr, sizeof( arr ) );
                server->Send(static_cast<void *>(arr));
                close(fd);
            }
            else if (!strncmp(recieved, "EXIT", 4)) {
        
                server->Send(const_cast<void *>(static_cast<const void *>("Successfully closed!")));
                break;
            }
            else {
                server->Send(const_cast<void *>(static_cast<const void *>("Unknown command")));
            }
        }
    }
    return 0; 
} 
