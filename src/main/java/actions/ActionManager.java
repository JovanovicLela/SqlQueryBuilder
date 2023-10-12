package actions;

public class ActionManager {

    private PrettyAction prettyAction;
    private RunAction runAction;
    private ImportAction importAction;
    private ExportAction exportAction;

    public ActionManager() {
        initialiseActions();
    }

    private void initialiseActions() {
        prettyAction = new PrettyAction();
        runAction = new RunAction();
        importAction = new ImportAction();
        exportAction = new ExportAction();
    }

    public PrettyAction getPrettyAction() {
        return prettyAction;
    }
    public RunAction getRunAction() {
        return runAction;
    }
    public ImportAction getImportAction() {
        return importAction;
    }
    public ExportAction getExportAction() {
        return exportAction;
    }
}
