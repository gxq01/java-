package client;
import client.connect.CodecUtil;
import common.entity.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
客户端各个功能与服务器连接、发送数据的类
 */
public class CScontrol {
    public static Socket socket;
    public static InputStream is;
    public static DataInputStream dis;
    public static OutputStream os;
    public static DataOutputStream dos;

    public static void baseConnect() throws IOException {
        //连接服务器
        socket = new Socket("127.0.0.1",8900);
        is = socket.getInputStream();
        dis = new DataInputStream(is);
        os = socket.getOutputStream();
        dos = new DataOutputStream(os);
    }
    public static void sendCommand(String command) throws IOException {
        //给服务器说明操作
        dos.writeUTF(command);
    }

    public static boolean loginToServer(String username,String password) throws IOException {
        try{
            baseConnect();
//          dos.writeUTF(username);//进入线程之前先提供userID供服务器加到hashmap里面去。。

            sendCommand("Login");
            dos.writeUTF(username);
            dos.writeUTF(password);

            //接受返回success或wrong
            String result = dis.readUTF(dis);
            System.out.println("登录结果是："+result);
            if(result.equals("success"))
                return true;
            else
                return false;

        }finally {
//            if (dos != null)
//                dos.close();
//            if (os != null)
//                os.close();
//            if (dis != null)
//                dis.close();
//            if (is != null)
//                is.close();
        }

    }

    public static int RegisterToServer(String username, String password) throws IOException {
        baseConnect();
        sendCommand("Register");
        dos.writeUTF(username);
        dos.writeUTF(password);

        String result = dis.readUTF(dis);
        if(result.equals("isExist"))
            return 0;//用户存在返回0
        else
            return 1;//注册成功返回1
    }
//    private static void shutStream() throws IOException {
//        if(dos!=null)
//            dos.close();
//        if(os!=null)
//            os.close();
//        if(dis!=null)
//            dis.close();
//        if(is!=null)
//            is.close();
//    }
    public static int getGoodsListLenToServer() throws Exception{
        try{
            //连接服务器获取列列表长度
            sendCommand("GetGoodsListLen");
            int listCount = dis.readInt();//获取到list的长度
            return listCount;
        }finally {
//            shutStream();
        }
    }//好像没什么用。。。
    public static List<Commodity> getGoodsListToServer() throws Exception{
        try{
            //连接服务器获取列列表<commodity>
            sendCommand("GetGoodsList");
            ObjectInputStream ois = new ObjectInputStream(is);
            List<Commodity> resultList;
            resultList = (List<Commodity>)ois.readObject();
            return resultList;
        }finally {
//            shutStream();
        }
    }

    public static int addGoodsToServer(String userID, String name, double price, int nums, int isAuction, Date postDate) throws Exception{
        try{

            sendCommand("AddGoods");
            //生成商品号码
            String goodID = CodecUtil.createOrderId();
            dos.writeUTF(goodID);
            dos.writeUTF(userID);
            dos.writeUTF(name);
            dos.writeDouble(price);
            dos.writeInt(nums);
            dos.writeInt(isAuction);
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(postDate);
            if(dis.readInt()==1){
                System.out.println("here");
                return 1;
            }else
                return 0;
        }finally {
//            shutStream();
        }
    }

    /*
    商品购买 添加成功返回1，失败返回0
     */
    //Commodity对象里有sellerID，所以只传来buyer就行
    public static int BuyToServer(Commodity commodity,User buyer,int nums) throws Exception{
        try{

            sendCommand("Buy");
            Date date = new Date();
            ObjectOutputStream oos = new ObjectOutputStream(os);

            //生成唯一订单号
            String oderID = CodecUtil.createOrderId();

            oos.writeObject(commodity);
            oos.writeObject(buyer);
            oos.writeObject(date);
            dos.writeInt(nums);//购买数量
            dos.writeUTF(oderID);
            int status = dis.readInt();
            if(status==1){//添加成功
                return 1;
            }else return 0;
        }finally {
//            shutStream();
        }
    }


    /*
    获取已发布商品
     */
    public static List<Commodity> getAlreadyPost(String userID) throws Exception {
        try{
            List<Commodity> commodities;

            sendCommand("getAlreadyPost");
            dos.writeUTF(userID);
            ObjectInputStream ois = new ObjectInputStream(is);
            commodities = (List<Commodity>) ois.readObject();
            return commodities;
        }finally {
//            shutStream();
        }
    }
    /*
    获取订单列表
     */
    public static List<Order> getOrderList(String userID) throws Exception {
        try{

            sendCommand("getOrderList");
            dos.writeUTF(userID);
            List<Order> orderList;
            ObjectInputStream ois = new ObjectInputStream(is);
            orderList=(List<Order>)ois.readObject();
            return orderList;
        }finally {
//            shutStream();
        }
    }

    /*
    获取指定商品的评论列表
     */
    public static List<Comment> getCommentList(String commodityID) throws Exception{
        try{

            sendCommand("getCommentList");
            dos.writeUTF(commodityID);
            List<Comment> commentList;
            ObjectInputStream ois = new ObjectInputStream(is);
            commentList = (List<Comment>)ois.readObject();
            return commentList;
        }finally {
//            shutStream();
        }

    }
    /*
    给指定商品添加评论
     */
    public static void addComment(String commodityID, String userID,String content)throws Exception{
        try{

            sendCommand("addComment");
            dos.writeUTF(commodityID);
            dos.writeUTF(userID);
            dos.writeUTF(content);
        }finally {
//            shutStream();
        }
    }

    public static List<User> getAllUsers() throws Exception{
            sendCommand("getAllUsers");
            System.out.println("嘿嘿嘿");
            ObjectInputStream ois = new ObjectInputStream(is);
            List<User> userList = (List<User>)ois.readObject();
            System.out.println(userList.get(5).getPassword());
            System.out.println("呵呵呵");
            return userList;
    }
}
