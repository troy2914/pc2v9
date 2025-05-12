package edu.csus.ecs.pc2.imports.ccs;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AutoJudgeSetting;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.PlaybackInfo;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.model.Site;

import edu.csus.ecs.pc2.clics.API202306.CLICSAccount;
import edu.csus.ecs.pc2.clics.API202306.CLICSContestInfo;
import edu.csus.ecs.pc2.clics.API202306.CLICSGroup;
import edu.csus.ecs.pc2.clics.API202306.CLICSOrganization;
import edu.csus.ecs.pc2.clics.API202306.CLICSProblem;
import edu.csus.ecs.pc2.clics.API202306.CLICSTeam;

public class ContestPackageFormatLoader implements IContestLoader {
    /**
     * Full content of json file.
     */
    private Map<String, Object> fullJSONContent = null;

    /**
     * Load Problem Data File Contents
     */
    private boolean loadProblemDataFiles = true;

    // these came from implementing IContestLoader...
    @Override
    public Problem addDefaultPC2Validator(Problem problem, int optionNumber) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addDefaultPC2Validator'");
    }

    @Override
    public void dumpSerialzedFileList(Problem problem, String logPrefixId, SerializedFile[] sfList) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'dumpSerialzedFileList'");
    }

    @Override
    public IInternalContest fromYaml(IInternalContest contest, String directoryName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'fromYaml'");
    }

    @Override
    public IInternalContest fromYaml(IInternalContest contest, String directoryName, boolean loadDataFileContents) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'fromYaml'");
    }

    @Override
    public IInternalContest fromYaml(IInternalContest contest, String[] yamlLines, String directoryName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'fromYaml'");
    }

    @Override
    public IInternalContest fromYaml(IInternalContest contest, String[] yamlLines, String directoryName, boolean loadDataFileContents) {
        HashMap<String, String[]> organizationsMap = null;
        contest = createContest(contest);

        File contestFile = new File(directoryName+File.separator+DEFAULT_CONTEST_JSON_FILENAME);
        if (contestFile.exists() && contestFile.canRead()) {
            CLICSContestInfo.fromJSON(contest);
            // TODO need to put duration in ContestTime
            // TODO need to put penalty_time somewhere
        } else {
            contestFile = new File(directoryName+File.separator+"config"+File.separator+DEFAULT_CONTEST_YAML_FILENAME);
            CLICSContestInfo.fromYAML(contest);
            // TODO need to put duration in ContestTime
            // TODO need to put penalty_time somewhere
        }
        File problemsFile = new File(directoryName+File.separator+DEFAULT_PROBLEMS_JSON_FILENAME);
        if (problemsFile.exists() && problemsFile.canRead()) {
            // TODO need to load the data too
            contest.addProblems(CLICSProblem.fromJSON(problemsFile));
        } else {
            problemsFile = new File(directoryName+File.separator+"config"+File.separator+DEFAULT_PROBLEMS_YAML_FILENAME);
            contest.addProblems(CLICSProblem.fromYAML(problemsFile));
        }
        File groupFile = new File(directoryName+File.separator+DEFAULT_GROUPS_JSON_FILENAME);
        if (groupFile.exists() && groupFile.canRead()) {
            Group[] groups = CLICSGroup.fromJSON(contest, groupFile, 0);
            for (int i=0 ; i < groups.length ; i++) {
                contest.addGroup(groups[i]);
            }
        }
        File organizationsFile = new File(directoryName+File.separator+DEFAULT_ORGANIZATIONS_JSON_FILENAME);
        if (organizationsFile.exists() && organizationsFile.canRead()) {
           // read this into a HashMap for use with teams
           organizationsMap = CLICSOrganization.fromJSON(organizationsFile);
        }
        File teamsFile = new File(directoryName+File.separator+DEFAULT_TEAMS_JSON_FILENAME);
        if (teamsFile.exists() && teamsFile.canRead()) {
            // fromJSON(IInternalContest contest, File jsonfile, int site, HashMap<String,String[]> institutionsMap) {
            // TODO change this to be a merge, like the snake laoder
            contest.addAccounts(CLICSTeam.fromJSON(contest, teamsFile, 1, organizationsMap));
        }
        File accountsFile = new File(directoryName+File.separator+DEFAULT_ACCOUNTS_JSON_FILENAME);
        if (accountsFile.exists() && accountsFile.canRead()) {
            try {
                Account[] accounts = CLICSAccount.fromJSON(contest, accountsFile, 1);
                // TODO another merge for account type of team
                // TODO contest.addAccounts(myAccounts.toArray(myAccountsArray);
                // TODO handle ip
                // TODO handle team_id
                // TODO maybe need to handle teams.json here too?
                // TODO maybe need to handle organization.json here too?
            } catch (IOException e) {
                StaticLog.log("error parsing "+DEFAULT_PROBLEMS_JSON_FILENAME,e);
                return null;
            }
        } else {
           // TODO look for accounts.yaml in directoryName/config/
            accountsFile = new File(directoryName+File.separator+"config"+File.separator+DEFAULT_ACCOUNTS_YAML_FILENAME);
            Account[] accounts = CLICSAccount.fromYAML(contest, accountsFile, 1);
        }
        throw new UnsupportedOperationException("Unimplemented method 'fromYaml'");
    }

    /**
     * Insures that contest is instantiated.
     *
     * Creates contest if contest is null, otherwise returns contest.
     *
     * @param contest
     * @return
     */
    private IInternalContest createContest(IInternalContest contest) {
        if (contest == null) {
            contest = new InternalContest();
            contest.setSiteNumber(1);
        }
        return contest;
    }

    @Override
    public AutoJudgeSetting[] getAutoJudgeSettings(String[] yamlLines, Problem[] problems) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAutoJudgeSettings'");
    }

    @Override
    public String[] getClarificationCategories(String[] yamlLines) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getClarificationCategories'");
    }

    @Override
    public String getContestTitle(String contestYamlFilename) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getContestTitle'");
    }

    @Override
    public String[] getFileNames(String directoryName, String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getFileNames'");
    }

    @Override
    public String[] getGeneralAnswers(String[] yamlLines) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getGeneralAnswers'");
    }

    @Override
    public String getJudgesCDPBasePath(String contestYamlFilename) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getJudgesCDPBasePath'");
    }

    @Override
    public Language[] getLanguages(String[] yamlLines) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getLanguages'");
    }

    @Override
    public String getProblemNameFromLaTex(String filename) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProblemNameFromLaTex'");
    }

    @Override
    public Problem[] getProblems(String[] contents, int defaultTimeOut) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProblems'");
    }

    @Override
    public Problem[] getProblems(String[] contents, int defaultTimeOut, long defaultMaxOutputSizeInBytes) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProblems'");
    }

    @Override
    public Problem[] getProblems(String[] contents, int defaultTimeOut, boolean loadDataFileContents, String defaultValidatorCommandLine) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProblems'");
    }

    @Override
    public Problem[] getProblems(String[] yamlLines, int seconds, boolean loadDataFileContents, String defaultValidatorCommand, String overrideValidatorCommandLine, boolean overrideUsePc2Validator,
            boolean manualReviewOverride) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProblems'");
    }

    @Override
    public Problem[] getProblems(String[] yamlLines, int seconds, long maxOutputSizeInBytes, boolean loadDataFileContents, String defaultValidatorCommand, String overrideValidatorCommandLine,
            boolean overrideUsePc2Validator, boolean manualReviewOverride) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProblems'");
    }

    @Override
    public Problem[] getProblemsFromLetters(Problem[] contestProblems, String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProblemsFromLetters'");
    }

    @Override
    public PlaybackInfo getReplaySettings(String[] yamlLines) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getReplaySettings'");
    }

    @Override
    public Site[] getSites(String[] yamlLines) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSites'");
    }

    @Override
    public boolean isLoadProblemDataFiles() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isLoadProblemDataFiles'");
    }

    @Override
    public Problem loadCCSProblemFiles(IInternalContest contest, String dataFileBaseDirectory, Problem problem, ProblemDataFiles problemDataFiles) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loadCCSProblemFiles'");
    }

    @Override
    public String[] loadFileWithIncludes(String dirname, String filename) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loadFileWithIncludes'");
    }

    @Override
    public String[] loadGeneralClarificationAnswers(String[] yamlLines) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loadGeneralClarificationAnswers'");
    }

    @Override
    public void loadPc2ProblemFiles(IInternalContest contest, String dataFileBaseDirectory, Problem problem, ProblemDataFiles problemDataFiles2, String dataFileName, String answerFileName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loadPc2ProblemFiles'");
    }

    @Override
    public void loadProblemInformationAndDataFiles(IInternalContest contest, String baseDirectoryName, Problem problem, boolean overrideUsePc2Validator) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loadProblemInformationAndDataFiles'");
    }

    @Override
    public void loadProblemInformationAndDataFiles(IInternalContest contest, String baseDirectoryName, Problem problem, boolean overrideUsePc2Validator, boolean overrideManualReview) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loadProblemInformationAndDataFiles'");
    }

    @Override
    public void setLoadProblemDataFiles(boolean loadProblemDataFiles) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setLoadProblemDataFiles'");
    }

    @Override
    public String unquote(String input, String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'unquote'");
    }

    @Override
    public String getCCSDataFileDirectory(String yamlDirectory, Problem problem) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCCSDataFileDirectory'");
    }

    @Override
    public String getCCSDataFileDirectory(String yamlDirectory, String shortDirName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCCSDataFileDirectory'");
    }

    @Override
    public boolean getBooleanValue(String string, boolean defaultBoolean) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBooleanValue'");
    }

    @Override
    public IInternalContest initializeContest(IInternalContest contest, File entry) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'initializeContest'");
    }

    @Override
    public File findCDPConfigDirectory(File entry) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findCDPConfigDirectory'");
    }

}
