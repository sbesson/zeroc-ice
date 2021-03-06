// **********************************************************************
//
// Copyright (c) 2003-2013 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

package test.Ice.udp;

import test.Ice.udp.Test.*;

public class AllTests
{
    private static void
    test(boolean b)
    {
        if(!b)
        {
            throw new RuntimeException();
        }
    }

    public static class PingReplyI extends _PingReplyDisp
    {
        public synchronized void
        reply(Ice.Current current)
        {
            ++_replies;
            notify();
        }

        public synchronized void
        reset()
        {
             _replies = 0;
        }

        public synchronized boolean
        waitReply(int expectedReplies, long timeout)
        {
            long end = System.currentTimeMillis() + timeout;
            while(_replies < expectedReplies)
            {
                long delay = end - System.currentTimeMillis();
                if(delay > 0)
                {
                    try
                    {
                        wait(delay);
                    }
                    catch(java.lang.InterruptedException ex)
                    {
                    }
                }
                else
                {
                    break;
                }
            }
            return _replies == expectedReplies;
        }

        private int _replies;
    }

    public static void
    allTests(Ice.Communicator communicator)
    {
        communicator.getProperties().setProperty("ReplyAdapter.Endpoints", "udp -p 12030");
        Ice.ObjectAdapter adapter = communicator.createObjectAdapter("ReplyAdapter");
        PingReplyI replyI = new PingReplyI();

        PingReplyPrx reply = 
            (PingReplyPrx)PingReplyPrxHelper.uncheckedCast(adapter.addWithUUID(replyI)).ice_datagram();
        adapter.activate();

        System.out.print("testing udp... ");
        System.out.flush();
        Ice.ObjectPrx base = communicator.stringToProxy("test -d:udp -p 12010");
        TestIntfPrx obj = TestIntfPrxHelper.uncheckedCast(base);

        int nRetry = 5;
        boolean ret = false;
        while(nRetry-- > 0)
        {
            replyI.reset();
            obj.ping(reply);
            obj.ping(reply);
            obj.ping(reply);
            ret = replyI.waitReply(3, 2000);
            if(ret)
            {
                break; // Success
            }

            // If the 3 datagrams were not received within the 2 seconds, we try again to
            // receive 3 new datagrams using a new object. We give up after 5 retries. 
            replyI = new PingReplyI();
            reply = (PingReplyPrx)PingReplyPrxHelper.uncheckedCast(adapter.addWithUUID(replyI)).ice_datagram();
        }
        test(ret == true);

        if(communicator.getProperties().getPropertyAsInt("Ice.Override.Compress") == 0)
        {
            //
            // Only run this test if compression is disabled, the test expect fixed message size
            // to be sent over the wire.
            //
            byte[] seq = null;
            try
            {
                seq = new byte[1024];
                while(true)
                {
                    seq = new byte[seq.length * 2 + 10];
                    replyI.reset();
                    obj.sendByteSeq(seq, reply);
                    replyI.waitReply(1, 10000);
                }
            }
            catch(Ice.DatagramLimitException ex)
            {
                test(seq.length > 16384);
            }
            obj.ice_getConnection().close(false);
            communicator.getProperties().setProperty("Ice.UDP.SndSize", "64000");
            seq = new byte[50000];
            try
            {
                replyI.reset();
                obj.sendByteSeq(seq, reply);
                test(!replyI.waitReply(1, 500));
            }
            catch(Ice.LocalException ex)
            {
                System.err.println(ex);
                test(false);
            }
        }

        System.out.println("ok");

        System.out.print("testing udp multicast... ");
        System.out.flush();
        String endpoint;
        if(communicator.getProperties().getProperty("Ice.IPv6").equals("1"))
        {
            if(System.getProperty("os.name").contains("OS X"))
            {
                endpoint = "udp -h \"ff02::1:1\" -p 12020 --interface \"lo0\"";
            }
            else
            {
                endpoint = "udp -h \"ff01::1:1\" -p 12020";
            }
        }
        else
        {
            endpoint = "udp -h 239.255.1.1 -p 12020";
        }
        base = communicator.stringToProxy("test -d:" + endpoint);
        TestIntfPrx objMcast = TestIntfPrxHelper.uncheckedCast(base);

        nRetry = 5;
        while(nRetry-- > 0)
        {
            replyI.reset();
            objMcast.ping(reply);
            ret = replyI.waitReply(5, 2000);
            if(ret)
            {
                break; // Success
            }
            replyI = new PingReplyI();
            reply = (PingReplyPrx)PingReplyPrxHelper.uncheckedCast(adapter.addWithUUID(replyI)).ice_datagram();
        }
        if(!ret)
        {
            System.out.println("failed (is a firewall enabled?)");
        }
        else
        {
            System.out.println("ok");
        }

        System.out.print("testing udp bi-dir connection... ");
        System.out.flush();
        obj.ice_getConnection().setAdapter(adapter);
        objMcast.ice_getConnection().setAdapter(adapter);
        nRetry = 5;
        while(nRetry-- > 0)
        {
            replyI.reset();
            obj.pingBiDir(reply.ice_getIdentity());
            obj.pingBiDir(reply.ice_getIdentity());
            obj.pingBiDir(reply.ice_getIdentity());
            ret = replyI.waitReply(3, 2000);
            if(ret)
            {
                break; // Success
            }
            replyI = new PingReplyI();
            reply = (PingReplyPrx)PingReplyPrxHelper.uncheckedCast(adapter.addWithUUID(replyI)).ice_datagram();
        }
        test(ret);
        System.out.println("ok");

        //
        // Sending the replies back on the multicast UDP connection doesn't work for most
        // platform (it works for OS X Leopard but not Snow Leopard, doesn't work on SLES,
        // Windows...). For Windows, see UdpTransceiver constructor for the details. So
        // we don't run this test.
        // 
//         System.out.print("testing udp bi-dir connection... ");
//         nRetry = 5;
//         while(nRetry-- > 0)
//         {
//             replyI.reset();
//             objMcast.pingBiDir(reply.ice_getIdentity());
//             ret = replyI.waitReply(5, 2000);
//             if(ret)
//             {
//                 break; // Success
//             }
//             replyI = new PingReplyI();
//             reply = (PingReplyPrx)PingReplyPrxHelper.uncheckedCast(adapter.addWithUUID(replyI)).ice_datagram();
//         }

//         if(!ret)
//         {
//             System.out.println("failed (is a firewall enabled?)");
//         }
//         else
//         {
//             System.out.println("ok");
//         }
    }
}
