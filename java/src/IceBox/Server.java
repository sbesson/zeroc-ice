// **********************************************************************
//
// Copyright (c) 2003-2009 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

package IceBox;

public final class Server extends Ice.Application
{
    private static void
    usage()
    {
        System.err.println("Usage: IceBox.Server [options] --Ice.Config=<file>\n");
        System.err.println(
            "Options:\n" +
            "-h, --help           Show this message.\n"
        );
    }

    public static void
    main(String[] args)
    {
        Ice.InitializationData initData = new Ice.InitializationData();
        initData.properties = Ice.Util.createProperties();
        initData.properties.setProperty("Ice.Admin.DelayCreation", "1");

        Server server = new Server();
        System.exit(server.main("IceBox.Server", args, initData));
    }

    public int
    run(String[] args)
    {
        for(int i = 1; i < args.length; ++i)
        {
            if(args[i].equals("-h") || args[i].equals("--help"))
            {
                usage();
                return 0;
            }
            else if(!args[i].startsWith("--"))
            {
                System.err.println("Server: unknown option `" + args[i] + "'");
                usage();
                return 1;
            }
        }

        ServiceManagerI serviceManagerImpl = new ServiceManagerI(communicator(), args);
        return serviceManagerImpl.run();
    }
}