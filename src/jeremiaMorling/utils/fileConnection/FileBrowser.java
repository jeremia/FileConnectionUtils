/*
 * FileBrowser.java
 *
 * Created on 8 ������ 2007, 20:50
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 *
 * Note: This source code is protected under the This software is licensed under the CC-GNU GPL.
 */

package jeremiaMorling.utils.fileConnection;

import java.util.*;
import javax.microedition.io.file.*;
import javax.microedition.lcdui.*;
import javax.microedition.io.*;
import jeremiaMorling.utils.vector.SortableVector;

/**
 *
 * @author Menny Even Danan (c) 2007. Note: This source code is protected under the This software is licensed under the CC-GNU GPL.
 */

public class FileBrowser extends List implements CommandListener, Runnable
{
    private String m_selectedValue;
    private String m_currentFoldersPath;
    private final IFileBrowserCreator m_manager;
    private Thread m_currentThread;

    private static String selectString = "Select";
    private static String cancelString = "Cancel";

    public static void localizeStrings( String select, String cancel ) {
        selectString = select;
        cancelString = cancel;
    }

    public FileBrowser(IFileBrowserCreator manager, String startPoint) 
    {
	super("Files", List.IMPLICIT);
	m_manager = manager;
	m_selectedValue = "";
	m_currentFoldersPath = startPoint;
	if (m_currentFoldersPath.indexOf('/')>0)
	    m_currentFoldersPath = m_currentFoldersPath.substring(0, m_currentFoldersPath.lastIndexOf('/')+1);
	m_currentThread = new Thread(this, "FileBrowser entries reader");
	m_currentThread.start();
	super.setSelectCommand( new Command( selectString , Command.OK, 1 ) );
	super.addCommand( new Command( cancelString, Command.CANCEL, 1 ) );
	super.setCommandListener(this);
    }

    public void commandAction(Command command, Displayable displayable) 
    {
	if (command.getCommandType() == Command.OK)
	{
	    try
	    {
		if (super.getSelectedIndex()>=0)
		{
		    String selectedItem = super.getString(super.getSelectedIndex());
		    System.out.println("Selected item is: "+selectedItem);
		    if (selectedItem.equals(".."))
		    {//one folder up
			m_selectedValue = "";
			m_currentFoldersPath = ReGenerateWithOneFolderUp(m_currentFoldersPath);
			System.out.println("Moving one folder up: " + m_currentFoldersPath);	
		    }
		    else
		    {//something selected
			m_selectedValue = selectedItem;
			System.out.println("New selectedValue: " + m_selectedValue);	
		    }
		    //why another thread? Must be done like this to prevent a deadlock
		    m_currentThread = new Thread(this, "FileBrowser entires reader");
		    m_currentThread.start();
		}
	    }
	    catch(Exception ex)
	    {
		ex.printStackTrace();
		m_manager.error(ex.getMessage());
	    }
	}
	else if (command.getCommandType() == Command.CANCEL)
	{
	    m_manager.cancel();
	}
    }

    private void buildFilesList() 
    {
	super.deleteAll();
	if ((m_currentFoldersPath.length() == 0) && (m_selectedValue.length() == 0))
	{//SE root elements
	    super.setTitle("Root");
	    System.out.println("Reading root list");
	    Enumeration roots = FileSystemRegistry.listRoots();
	    while(roots.hasMoreElements()) {
                super.append(roots.nextElement().toString(), null);
	    }
	}
	else
	{//removing the filename, staying with the path, and openning it
	    try
	    {
		String path = m_currentFoldersPath;
		if (m_selectedValue.length() > 0) {
		    path = path + m_selectedValue;
		}
		System.out.println("Opening: " + path);
		FileConnection fs = null;
		try {
		    fs = (FileConnection)Connector.open("file:///" + path, Connector.READ);
		    if (fs.isDirectory()) {
			m_currentFoldersPath = path;
			System.out.println("Reading list of " + path);
                        
			super.append("..", null);
                        SortableVector dirsAndFiles = FileConnectionUtil.getFilesAndDirectories( fs, "*.jpg" );
                        for( int i=0; i<dirsAndFiles.size(); i++ )
                            super.append( dirsAndFiles.elementAt( i ).toString(), null );

		    }
		    else {
			System.out.println("Selected a new file " + path);
			System.out.println("Selected a file : "+path);
			m_manager.fileSelected(path);
			return;//no need to update list
		    }
		}
		finally {
                    super.setTitle(m_currentFoldersPath);
		    try {
			if (fs != null)
			    fs.close();
		    }
		    catch(Exception ex) {}
		}
	    }
	    catch(Exception ex)
	    {
		m_manager.error(ex.getMessage());
		return;//error!
	    }
	}
    }

    public void run() 
    {
	buildFilesList();
    }

    private static String ReGenerateWithOneFolderUp(String currentFoldersPath) 
    {
	//in case of no-up (i.e., 'root1/'), will return empty string
	int firstSlash = currentFoldersPath.indexOf('/');
	if (firstSlash == -1) {//if no slashes, will move to roots (empy string)
	    return "";
	}
        else {
	    int lastSlash = currentFoldersPath.lastIndexOf('/');
	    if (firstSlash == lastSlash) {
		System.out.println("From " + currentFoldersPath + " moving to roots! Just the root folder left");
		return "";
	    }
            else {
		String withoutLastSlash = currentFoldersPath.substring(0, lastSlash);
		int secondLastSlash = withoutLastSlash.lastIndexOf('/');
		String withoudSecondLastSlash = withoutLastSlash.substring(0, secondLastSlash);
		System.out.println("From " + currentFoldersPath + " moving to " + withoudSecondLastSlash);
		return withoudSecondLastSlash + '/';//I need the last slash
	    }
	}
    }
}
