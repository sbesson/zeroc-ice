// **********************************************************************
//
// Copyright (c) 2003-2013 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

using System;
using System.Diagnostics;
using System.Reflection;

[assembly: CLSCompliant(true)]

[assembly: AssemblyTitle("IceTest")]
[assembly: AssemblyDescription("Ice test")]
[assembly: AssemblyCompany("ZeroC, Inc.")]

public sealed class DummyLogger : Ice.Logger
{
    public void print(string message)
    {
    }

    public void trace(string category, string message)
    {
    }

    public void warning(string message)
    {
    }

    public void error(string message)
    {
    }

    public Ice.Logger cloneWithPrefix(string prefix)
    {
        return new DummyLogger();
    }
}

public class Server
{
    private static int run(string[] args, Ice.Communicator communicator)
    {
        Ice.Properties properties = communicator.getProperties();
        // We don't need to disable warnings because we have a dummy logger.
        //properties.setProperty("Ice.Warn.Dispatch", "0");
        properties.setProperty("TestAdapter.Endpoints", "default -p 12010:udp");
        Ice.ObjectAdapter adapter = communicator.createObjectAdapter("TestAdapter");
        Ice.Object @object = new ThrowerI();
        adapter.add(@object, communicator.stringToIdentity("thrower"));
        adapter.activate();
        communicator.waitForShutdown();
        return 0;
    }

    public static int Main(string[] args)
    {
        int status = 0;
        Ice.Communicator communicator = null;

        try
        {
            //
            // For this test, we need a dummy logger, otherwise the
            // assertion test will print an error message.
            //
            Ice.InitializationData initData = new Ice.InitializationData();
            initData.logger = new DummyLogger();
            communicator = Ice.Util.initialize(ref args, initData);
            status = run(args, communicator);
        }
        catch(System.Exception ex)
        {
            System.Console.WriteLine(ex);
            status = 1;
        }

        if(communicator != null)
        {
            try
            {
                communicator.destroy();
            }
            catch(Ice.LocalException ex)
            {
                System.Console.WriteLine(ex);
                status = 1;
            }
        }

        return status;
    }
}
