package player.gamer.python;

import util.gdl.grammar.GdlSentence;
import util.logging.GamerLogger;

import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

import player.gamer.Gamer;
import player.gamer.exception.MetaGamingException;
import player.gamer.exception.MoveSelectionException;

/**
 * PythonGamer is a superclass that allows you to hook Python gamers into the
 * rest of the Java framework. In order to do this, do the following:
 * 
 * 1) Create a subclass of PythonGamer that overrides getPythonGamerName() and
 *    getPythonGamerModule() to indicate where the Python source code file is.
 *    This is the Java stub that refers to the real Python gamer class.
 *    
 * 2) Create the Python source code file, in the /pysrc/ directory in the root
 *    directory for this project. Make sure that the stub points to this class,
 *    and that the Python class is a valid subclass of Gamer.
 *    
 * For examples where this has already been done, see @PythonLegalGamerStub and
 * @PythonRandomGamerStub, which are both implemented in Python and hook into
 * the Java framework using the PythonGamer stubs.
 * 
 * @author Sam
 */
public abstract class PythonGamer extends Gamer
{
    Gamer thePythonGamer;
    
    protected abstract String getPythonGamerName();
    protected abstract String getPythonGamerModule();        

    public PythonGamer() {
        super();
        
        try {
            // Load in the Python gamer, using a Jython intepreter.
            //
            // TODO: The sys path modification is very hacky, and I strongly suspect
            //       that there is a cleaner solution. Ideally we would be able to store
            //       Python files in directories as well, and address the directories in
            //       the same way Java does (i.e. "directory.subdirectory.file").
            PythonInterpreter interpreter = new PythonInterpreter();
            interpreter.exec("import sys");
            interpreter.exec("sys.path.append(\"__pyclasspath__/../pysrc/\")");
            interpreter.exec("from " + getPythonGamerModule() + " import " + getPythonGamerName());
            PyObject thePyClass = interpreter.get(getPythonGamerName());
            PyObject PyGamerObject = thePyClass.__call__();
            thePythonGamer = (Gamer)PyGamerObject.__tojava__(Gamer.class);
        } catch(Exception e) {
            GamerLogger.logError("GamePlayer", "Caught exception in Python initialization:");
            GamerLogger.logStackTrace("GamePlayer", e);
        }
    }
    
    // The following methods are overriden as 'final' because they should not
    // be changed in subclasses of this class. Subclasses of this class should
    // only implement getPythonGamerName() and getPythonGamerModule(), and then
    // implement the real methods in the actual Python gamer. Essentially, any
    // subclass of this class is a Java-implementation stub for the actual real
    // Python implementation.
    
    @Override
    public final void metaGame(long timeout) throws MetaGamingException {
        thePythonGamer.setMatch(getMatch());
        thePythonGamer.setRoleName(getRoleName());
        try {
            thePythonGamer.metaGame(timeout);
        } catch(RuntimeException e) {
            GamerLogger.logError("GamePlayer", "Caught exception in Python stateMachineMetaGame:");
            e.printStackTrace();
        }
    }
    
    @Override
    public final GdlSentence selectMove(long timeout) throws MoveSelectionException {
        thePythonGamer.setMatch(getMatch());
        thePythonGamer.setRoleName(getRoleName());
        try {
            return thePythonGamer.selectMove(timeout);
        } catch(RuntimeException e) {
            GamerLogger.logError("GamePlayer", "Caught exception in Python stateMachineSelectMove:");
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public final String getName() {
        try {
            return thePythonGamer.getName();
        } catch(RuntimeException e) {
            GamerLogger.logError("GamePlayer", "Caught exception in Python getName:");
            e.printStackTrace();
            return this.getClass().getSimpleName();
        }
    }    
}