
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
    system("while sleep 3; do top > top.txt done");
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
        valread = read( new_socket , recieved, 1024);
    }

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
        //int i = 3;
        //while (i--) {
            server->WaitForReceive(recieved);
            printf("Recieved Message: %s", recieved);
            if (!strncmp(recieved, "GET_SYSTEM_INFO", 15)) {
        // int err = pthread_create(&topCommandThread, NULL, &topThread, NULL);
        // if (err != 0)
        //     printf("\ncan't create thread :[%s]", strerror(err));
        // pthread_join(topCommandThread, NULL);
        // char arr[300];
        // int size_read;
        // int fd = open("top.txt", O_RDONLY);
        // size_read = read( fd, arr, sizeof( arr ) );
                server->Send(static_cast<void *>(sendData));
            }
       //}
    }


//     int server_fd, new_socket, valread; 
//     struct sockaddr_in address; 
//     int opt = 1; 
//     int addrlen = sizeof(address); 
//     char buffer[1024] = {0}; 
//     char *hello = strdup("Hello from server"); 
       
//     // Creating socket file descriptor 
//     if ((server_fd = socket(AF_INET, SOCK_STREAM, 0)) == 0) 
//     { 
//         perror("socket failed"); 
//         exit(EXIT_FAILURE); 
//     } 
       
//     // Forcefully attaching socket to the port 8080 
//     if (setsockopt(server_fd, IPPROTO_TCP, TCP_NODELAY, 
//                                                   &opt, sizeof(opt))) 
//     { 
//         perror("setsockopt"); 
//         exit(EXIT_FAILURE); 
//     } 
//     address.sin_family = AF_INET; 
//     address.sin_addr.s_addr = ((in_addr_t) 0x00000000); 
//     address.sin_port = htons( PORT ); 
       
//     // Forcefully attaching socket to the port 8080 
//     if (bind(server_fd, (struct sockaddr *)&address,  
//                                  sizeof(address))<0) 
//     { 
//         perror("bind failed"); 
//         exit(EXIT_FAILURE); 
//     } 
//     if (listen(server_fd, 3) < 0) 
//     { 
//         perror("listen"); 
//         exit(EXIT_FAILURE); 
//     } 
//     if ((new_socket = accept(server_fd, (struct sockaddr *)&address,  
//                        (socklen_t*)&addrlen))<0) 
//     { 
//         perror("accept"); 
//         exit(EXIT_FAILURE); 
//     }
//     valread = read( new_socket , buffer, 1024); 
//     printf("%s\n",buffer );
//     int err = pthread_create(&topCommandThread, NULL, &topThread, NULL);
//         if (err != 0)
//             printf("\ncan't create thread :[%s]", strerror(err));
//     pthread_join(topCommandThread, NULL);
//     char arr[300];
//     int size_read;
//     int fd = open("top.txt", O_RDONLY);
//     size_read = read( fd, arr,
//                       sizeof( arr ) );

//     /* Test for error */
//     if( size_read == -1 ) {
//         perror( "Error reading myfile.dat" );
//         return EXIT_FAILURE;
//     }
//     printf("%s\n\n", arr);
//     /* Close the file */
//     if (!strncmp(buffer, "GET_SYSTEM_INFO", 15)) {
//     std::ifstream myReadFile;
//     //x86 code
//     // myReadFile.open("/proc/cpuinfo");
//     // std::string str;
//     // if (myReadFile.is_open()) {
//     //     while (std::getline(myReadFile, str)) {
//     //         if (!strncmp("model name", str.c_str(), 10)) {
//     //             std::cout << str << std::endl;
//     //             break;
//     //         }
//     //     }
//     // }
//     // }
//     //     else
//     //         std::cout<< "Open failed\n";
    
//     //     myReadFile.close();
    
//     //aarch64le QNX code
//     // struct stat buf;
//     // char str[30];
//     // if (stat("/proc", &buf) != -1) {
//     //     sprintf(str, "%d", buf.st_size);
//     // }
    
//     // send(new_socket , str , strlen(str) , 0); 
//     send(new_socket , arr , strlen(arr) , 0); 
//     printf("Hello message sent\n");
//     close(fd);
// }
    return 0; 
} 
