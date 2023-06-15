在线测试地址:

服务端：localhost:8086/admin

客户端: localhost:8086/index

#### 服务端当前在线数和在线人不是异步的，接受客户端的信息是异步的，所有在所有用户连接完成后刷新一次服务端就好

- 支持给一人推送信息，多人推送以及全部推送 个人推送和多人推送只要在多选框选择要推送的人，然后点击发送  全部推送 只需点击全部发送就好


![演示gif](https://suyu-img.oss-cn-shenzhen.aliyuncs.com/demo.gif)

### Websocket

Q: Websocket是什么？

A: 是一种在单个TCP连接上进行全双工通信的协议。

Q: 作用是什么？

A: 使得客户端和服务器之间的数据交换变得更加简单，允许服务端主动向客户端推送数据。在WebSocket API中，
浏览器和服务器只需要完成一次握手，两者之间就直接可以创建持久性的连接，并进行双向数据传输。

Q: 优缺点是什么？

A：优点：客户端与服务端之间通讯取代了原先轮询的方式，能更好的节省服务器资源和带宽，并且能够更实时地进行通讯。
   缺点: 不支持低端的浏览器，这种情况就只能采用之前的轮询方式。
   
   
```js
//传统轮询询问方式
var ask = function() {
    
  $.ajax({
        url:'轮询地址',
        data:"user=client1",
        type:'post',
        success:function(res){
          if(res.data.status == true){
      
            console.log('服务端推送信息:%s',res.data.message)
            //TODO 对服务端推送的信息进行处理
            //清除定时器
            clearInterval(loopAsk);
          }
        }
      })
}

var loopAsk = setTimeout(ask,1000);

                                   
客户端:我是普通用户客户端1有我的信息嘛
                                          服务端:我查一下,没有                                          
客户端:我是普通用户客户端1有我的信息嘛
                                          服务端:我再查一下,还是没有
客户端:我是普通用户客户端1有我的信息嘛
                                          服务端:我再查一下,还是没有(已经有点不耐烦)                                         
客户端:我是普通用户客户端1有我的信息嘛
                                          服务端:我再查一下,谢天谢地,有了,终于可以摆脱这个穷逼啦.信息内容是:你妈叫你回家吃饭
最后客户端拿着信息回家吃饭啦
```   

```js
//websocket方式
var ws = null;

//和服务端进行连接的
if ('WebSocket' in window){
   ws = new WebSocket("ws://127.0.0.1:8086/socketServer/client2");
   
} else{
    console.log("该浏览器不支持websocket");    
}   

//接受来自服务端推送的信息
ws.onmessage = function(evt) {
    
    console.log('服务端推送信息:%s',evt.data)
    
};    

//断开连接触发	        
ws.onclose = function(evt) {
    
   console.log("client2成功断开服务端") 
    
};    

//和服务端连接成功触发	        
ws.onopen = function(evt) {
    
    console.log("client2连接服务端成功")
    
};  

客户端2: 那谁你过来一下,我要在你这办个会员,有我的信息记得发送给我。
                                                        服务端：好的,老板请把你的联系方式留下。

                      此时有一条某某发送给客户端2的信息,看到信息后服务端立马拿起啦手中的电话打给客户端2
                      
                                                        服务端：老板，这里有条您的信息，内容是：老公，你单手开法拉利的样子真帅。
                                                        
                      此时又有一条某某发送给客户端2的信息,看到信息后服务端立马拿起啦手中的电话打给客户端2
                      
                                                        服务端：老板，这里有条您的信息，内容是： xxxxx                      
              
```
