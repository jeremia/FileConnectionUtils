/*
 * IFileBrowserCreator.java
 *
 * Created on 12 ������ 2007, 16:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 *
 * Note: This source code is protected under the This software is licensed under the CC-GNU GPL.
 */

package jeremiaMorling.utils.fileConnection;

/**
 *
 * @author Menny Even Danan (c) 2007. Note: This source code is protected under the This software is licensed under the CC-GNU GPL.
 */
public interface IFileBrowserCreator
{
    void fileSelected( String path );
    void cancel();
    void error( String error );
}
