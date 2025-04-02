package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.openqa.selenium.By;

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
        FRIENDS("/people/friends"),
        ALL_PEOPLE("/people/all");
        private final String displayName;

    }

    private final ElementsCollection friendRows = $$("#friends tr");
    private final ElementsCollection allPeopleRows = $$("#all tr");
    private final String baseTabLocator = "//a[contains(@href, '%s')]";
    private final String baseButtonLocator = "//button[contains(text(), '%s')]";

    public void assertPersonIsPresentInList(String personName, Tabs tab) {
        openTab(tab);

        switch (tab) {
            case FRIENDS:
                friendRows.find(text(personName)).shouldBe(visible, Duration.ofSeconds(5));
                break;
            case ALL_PEOPLE:
                allPeopleRows.find(text(personName)).shouldBe(visible, Duration.ofSeconds(5));
                break;
        }
    }

    public  void assertListIsEmpty(Tabs tab){
        switch (tab) {
        case FRIENDS:
        friendRows.shouldBe(empty);
        break;
        case ALL_PEOPLE:
        allPeopleRows.shouldBe(empty);
        break;
    }}

    private void openTab(Tabs tabName) {
        $x(format(baseTabLocator, tabName.getDisplayName())).click();
    }


    private void assertIncomeFiendRequestIsShown(ElementsCollection friendRows, String friendName  ){
        var friendRow = friendRows.find(text(friendName));
        friendRow.find(xpath(format(baseButtonLocator,"Accept"))).shouldBe(visible);
        friendRow.find(xpath(format(baseButtonLocator,"Decline"))).shouldBe(visible);

// для конкретного пользователя  горит кнопка assept + decline
    }


    private void assertOutcomeFriendRequestIsShown(ElementsCollection friendRows, String friendName){
        var friendRow = friendRows.find(text(friendName));
        friendRow.find(xpath("//span[contains(text(), 'Waiting...')]")).shouldBe(visible);

// для конкретного пользователя не горит кнопка add friend  и на его месте текст Waiting...
    }

    private  void assertButtonIsEnabled(String buttonName){
        $x(format(baseButtonLocator, buttonName)).shouldBe(Condition.enabled);
    }

}
