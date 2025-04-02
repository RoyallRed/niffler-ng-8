package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;

import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$x;
import static java.lang.String.*;
import static org.openqa.selenium.By.*;

public class PeoplePage {

    @Getter
    @AllArgsConstructor
    public enum Tabs {
        FRIENDS_TAB("/people/friends"),
        ALL_PEOPLE_TAB("/people/all");
        private final String displayName;
    }

    @Getter
    @AllArgsConstructor
    public enum Lists {
        FRIENDS_LIST("friends"),
        ALL_PEOPLE_LIST("all"),
        REQUESTS_LIST("requests");
        private final String displayName;
    }


    private final ElementsCollection friendRequestRows = $$("#requests  tr");
    private final ElementsCollection friendRows = $$("#friends tr");
    private final ElementsCollection allPeopleRows = $$("#all tr");
    private final String baseTabLocator = "//a[contains(@href, '%s')]";
    private final String baseButtonLocator = ".//button[contains(text(), '%s')]";
    private final String baseListLocator = "#%s  tr";


    public void assertListIsEmpty(Lists list) {
        switch (list) {
            case FRIENDS_LIST:
                friendRows.shouldBe(empty);
                break;
            case ALL_PEOPLE_LIST:
                allPeopleRows.shouldBe(empty);
            case REQUESTS_LIST:
                friendRequestRows.shouldBe(empty);
                break;
        }
    }

    public PeoplePage openTab(Tabs tabName) {
        $x(format(baseTabLocator, tabName.getDisplayName())).click();
        return this;
    }


    public void assertIncomeFiendRequestIsShown(String personName, Lists listName) {
        var row = $$(String.format(baseListLocator, listName.getDisplayName())).find(text(personName));
        row.shouldBe(visible, Duration.ofSeconds(5));
        row.find(xpath(format(baseButtonLocator, "Accept"))).shouldBe(visible);
        row.find(xpath(format(baseButtonLocator, "Decline"))).shouldBe(visible);
    }

    public void assertOutcomeFiendRequestIsShown(String personName, Lists listName) {
        var row = $$(String.format(baseListLocator, listName.getDisplayName())).find(text(personName));
        row.shouldBe(visible, Duration.ofSeconds(5));
        row.find(xpath(".//span[contains(text(), 'Waiting...')]")).shouldBe(visible);

    }

    public void assertFriendIsPresentInFriendList(String friendName){
        friendRows.find(text(friendName)).shouldBe(visible);

    }

/*    public void assertIncomeFiendRequestIsShown(String friendName) {
        var friendRow = friendRequestRows.find(text(friendName));
        friendRow.find(xpath(format(baseButtonLocator, "Accept"))).shouldBe(visible);
        friendRow.find(xpath(format(baseButtonLocator, "Decline"))).shouldBe(visible);

    }*/


    private void assertOutcomeFriendRequestIsShown(ElementsCollection friendRows, String friendName) {
        var friendRow = friendRows.find(text(friendName));
        friendRow.find(xpath("//span[contains(text(), 'Waiting...')]")).shouldBe(visible);

// для конкретного пользователя не горит кнопка add friend  и на его месте текст Waiting...
    }

    private void assertButtonIsEnabled(String buttonName) {
        $x(format(baseButtonLocator, buttonName)).shouldBe(Condition.enabled);
    }

}
