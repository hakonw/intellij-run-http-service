package bid.bepis.actionapi;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.ide.RestService;

import java.io.IOException;
import java.util.Optional;

// Inspiration
// https://github.com/centic9/IntelliJ-Automation-Plugin/blob/master/src/main/java/org/dstadler/intellij/automation/RESTService.java
// org/jetbrains/ide/OpenFileHttpService.kt

public class RunBuildHttpService extends RestService {

    public boolean isMethodSupported(@NotNull HttpMethod method) {
        return method == HttpMethod.GET || method == HttpMethod.POST;
    }

    @Nullable
    @Override
    public String execute(@NotNull QueryStringDecoder queryStringDecoder, @NotNull FullHttpRequest fullHttpRequest, @NotNull ChannelHandlerContext channelHandlerContext) {
        // Get argument from query
        // Find action
        // run action

        // urlDecoder.parameters()[name]?.lastOrNull()
        Optional<String> actionId = queryStringDecoder.parameters().get("command").stream().findAny();

        if (actionId.isEmpty()) {
            return "query argument command not found";
        }

        boolean anySuccessful = false;

        for (Project project : ProjectManager.getInstance().getOpenProjects()) {
            AnActionEvent event = AnActionEvent.createFromDataContext(actionId.get(), null, dataId -> {
                if (dataId.equals(CommonDataKeys.PROJECT.getName())) {
                    return project;
                }
                return null;
            });

            ActionManager actionManager = ActionManager.getInstance();
            if (actionManager == null) {
                break;
            }

            AnAction action = actionManager.getAction(actionId.get());
            if (action == null) {
                return "Could not find action " + actionId.get();
            }
            action.actionPerformed(event);
            anySuccessful = true;
        }

        if (!anySuccessful) {
            return "Could not find any action managers in any open projects";
        }
        return null;
    }

    @NotNull
    @Override
    protected String getServiceName() {
        return "run";
    }
}