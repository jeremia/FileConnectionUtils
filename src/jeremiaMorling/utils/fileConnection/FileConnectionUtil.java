/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jeremiaMorling.utils.fileConnection;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Enumeration;
import javax.microedition.io.file.FileConnection;
import jeremiaMorling.utils.vector.ComparableString;
import jeremiaMorling.utils.vector.SortableVector;

/**
 * Utility class for FileConnections.
 *
 * @author Jeremia
 */
public class FileConnectionUtil {
    private static final String CHAR_ENC_UTF_8 = "UTF-8";
    
    /**
     * This class is not meant to be instansiated.
     */
    private FileConnectionUtil(){};

    /**
     * Returns a list of directories and files specified by the filters.
     * The list is of type SortableVector and contains type ComparableString.
     * Directories are always added and sorted first. Then all the files are added and sorted.
     *
     * @param fc FileConnection to be used.
     * @param filter A list of file filters, one per element in the array.
     * @return  A sorted list of directories and files specified by the filter.
     * @throws IOException If method fails to list files and directories at all.
     */
    public static SortableVector getFilesAndDirectories( FileConnection fc, String filters[] ) throws IOException {
        SortableVector dirsAndFiles = new SortableVector();
        Enumeration e = fc.list();
        ComparableString name;

        // Add directories first.
	while(e.hasMoreElements()) {
            name = new ComparableString( e.nextElement().toString() );
            if( name.endsWith( "/" ) ) {
                dirsAndFiles.addElement( name );
                /*SortableVector test = new SortableVector();
                test.addElement( name );
                return test;*/
            }
        }
        dirsAndFiles.sort();

        // Then add files from from filters.
        SortableVector files = new SortableVector();
        for( int i=0; i<filters.length; i++ ) {
            e = fc.list( filters[i], false );
            while(e.hasMoreElements()) {
                name = new ComparableString( e.nextElement().toString() );
                // The following is due to inconsequences in if dirs are listed or not when calling list( filter, includeHidden ).
                if( !(name.endsWith( "/" )) )
                    files.addElement( name );
            }
        }
        files.sort();

        dirsAndFiles.addVector( files );

        return dirsAndFiles;
    }

    /**
     * Returns a list of directories and files specified by the filter.
     * The list is of type SortableVector and contains type ComparableString.
     * Directories are always added and sorted first. Then all the files are added and sorted.
     *
     * @param fc FileConnection to be used.
     * @param filter A list of file filters, one per element in the array.
     * @return  A sorted list of directories and files specified by the filter.
     * @throws IOException If method fails to list files and directories at all.
     */
    public static SortableVector getFilesAndDirectories( FileConnection fc, String filter ) throws IOException {
        String filters[] = new String[1];
        filters[0] = filter;
        return getFilesAndDirectories( fc, filters );
    }
    
    public static String readTextFile( String path ) throws IOException {
        return readTextFile( path, CHAR_ENC_UTF_8 );
    }
    
    public static String readTextFile( String path, String encoding ) throws IOException {
        Reader in = null;
        try {
            in = new InputStreamReader( FileConnection.class.getResourceAsStream( path ), encoding );
            StringBuffer temp = new StringBuffer( 1024 );
            char[] buffer = new char[1024];
            int read;
            while( (read = in.read( buffer, 0, buffer.length )) != -1 ) {
                temp.append( buffer, 0, read );
            }
            try {
                in.close();
            } catch( IOException ex ) {
            }
            return temp.toString();
        } catch( IOException e1 ) {
            try {
                in.close();
            } catch( IOException e2 ) {
            }
            throw e1;
        }
    }
}
