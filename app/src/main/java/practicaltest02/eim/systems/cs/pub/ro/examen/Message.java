package practicaltest02.eim.systems.cs.pub.ro.examen;

/**
 * Created by vlad on 5/21/18.
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by Apex on 15/05/2018.
 */

public class Message implements Serializable
{
    String s;

    public Message(String s)
    {
        this.s = s;
    }

    public static Message fromString(String s) throws IOException, ClassNotFoundException
    {
        byte[] data = Base64Coder.decode(s);
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data));

        Message m = (Message)objectInputStream.readObject();
        objectInputStream.close();

        return m;
    }

    public static String toString(Message m) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(m);
        objectOutputStream.close();

        return new String(Base64Coder.encode(byteArrayOutputStream.toByteArray()));
    }
}
