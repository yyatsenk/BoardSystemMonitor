
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
#include <pthread.h>
#define PORT 5005 

void *topThread(void *p) {
    (void)p;
    system("top -i 1 | grep -E -- 'Memory|CPU' > top.txt");
    return 0;
}

class MessagingVCU {
    int server_fd = -1;
    int new_socket;
    int valread;
    int opt = 1;
    int addrlen = sizeof(address); 
    char buffer[1024] = {0}; 
    struct sockaddr_in address;
    static MessagingVCU *sr;
    
    MessagingVCU() {
        if ((server_fd = socket(AF_INET, SOCK_STREAM, 0)) == 0) 
            perror("socket failed"); 
    }
    public:
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
            perror("listen"); 
            exit(EXIT_FAILURE); 
        } 
        return EXIT_SUCCESS;
    }
    
    void WaitForReceive(char *recieved) {
        
        if ((new_socket = accept(server_fd, (struct sockaddr *)&address,  
                       (socklen_t*)&addrlen))<0) 
        { 
            perror("accept"); 
            exit(EXIT_FAILURE); 
        }
        recv(new_socket,recieved,1024,0);
        //valread = read( new_socket , recieved, 1024);
    }

    int Send(void *data) {
        if (server_fd < 0)
            return EXIT_FAILURE;
        send(new_socket , static_cast<char*>(data) , strlen(static_cast<char*>(data)) , 0);
        close(new_socket);
        return EXIT_SUCCESS;
    }
    ~MessagingVCU() {
        close(new_socket);
        close(server_fd);
    }
    static MessagingVCU * getMessagingVCU() {
        if (sr != nullptr)
            return sr;
        sr = new MessagingVCU();
        return sr;
    }
};
MessagingVCU * MessagingVCU::sr = 0;

int main(int argc, char const *argv[]) 
{ 
    pthread_t topCommandThread;
    char recieved[1024] = {0};
    MessagingVCU *server = MessagingVCU::getMessagingVCU();
    char *sendData = strdup("Hello from server");
    if (!server->Open()) {
        while (true) {
        server->WaitForReceive(recieved);
        printf("Recieved Message: %s", recieved);
        if (!strncmp(recieved, "GET_SYSTEM_INFO", 15)) {
        
            int err = pthread_create(&topCommandThread, NULL, &topThread, NULL);
            if (err != 0)
                printf("\ncan't create thread :[%s]", strerror(err));
            pthread_join(topCommandThread, NULL);
            
            char arr[150];
            int size_read;
            int fd = open("top.txt", O_RDONLY);
            size_read = read( fd, arr, sizeof( arr ) );
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
