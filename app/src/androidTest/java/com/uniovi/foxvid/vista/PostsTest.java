package com.uniovi.foxvid.vista;


import androidx.annotation.NonNull;
import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uniovi.foxvid.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class PostsTest {

    UiDevice mDevice;

    @Before
    public void before() throws Exception {
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    @Rule
    public ActivityScenarioRule<Login> mActivityTestRule = new ActivityScenarioRule<>(Login.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION");

    @Test
    public void postsTest() throws UiObjectNotFoundException {
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btGoogle), withText("Google"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.idLoginLayaut),
                                        1),
                                1),
                        isDisplayed()));
        appCompatButton.perform(click());

        UiObject mText = mDevice.findObject(new UiSelector().text("NONE OF THE ABOVE"));
        mText.click();

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.idRvPost),
                        withParent(allOf(withId(R.id.swipeRefreshLayout),
                                withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class)))),
                        isDisplayed()));
        recyclerView.check(matches(isDisplayed()));

        ViewInteraction imageButton = onView(
                allOf(withId(R.id.btnNewPost),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class))),
                        isDisplayed()));
        imageButton.check(matches(isDisplayed()));

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.btnNewPost),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.FrameLayout")),
                                        1),
                                0),
                        isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction imageButton2 = onView(
                allOf(withContentDescription("Desplazarse hacia arriba"),
                        withParent(allOf(withId(R.id.new_post_toolbar),
                                withParent(withId(R.id.topAppBarNewPost)))),
                        isDisplayed()));
        imageButton2.check(matches(isDisplayed()));

        ViewInteraction textView = onView(
                allOf(withId(R.id.btn_publish_post), withText("PUBLICAR"),
                        withParent(withParent(withId(R.id.new_post_toolbar))),
                        isDisplayed()));
        textView.check(matches(withText("PUBLICAR")));

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.txtNewPost),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.coordinatorlayout.widget.CoordinatorLayout")),
                                        1),
                                0),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("Prueba de espresso"), closeSoftKeyboard());

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.btn_publish_post), withText("Publicar"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.new_post_toolbar),
                                        1),
                                0),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.txtPost), withText("Prueba de espresso"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView2.check(matches(withText("Prueba de espresso")));

        ViewInteraction floatingActionButton2 = onView(
                allOf(withId(R.id.btnNewPost),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.FrameLayout")),
                                        1),
                                0),
                        isDisplayed()));
        floatingActionButton2.perform(click());

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Desplazarse hacia arriba"),
                        childAtPosition(
                                allOf(withId(R.id.new_post_toolbar),
                                        childAtPosition(
                                                withId(R.id.topAppBarNewPost),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction recyclerView2 = onView(
                allOf(withId(R.id.idRvPost),
                        withParent(allOf(withId(R.id.swipeRefreshLayout),
                                withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class)))),
                        isDisplayed()));
        recyclerView2.check(matches(isDisplayed()));

        ViewInteraction imageButton3 = onView(
                allOf(withId(R.id.btSettings),
                        withParent(withParent(withId(R.id.appBarLayout))),
                        isDisplayed()));
        imageButton3.check(matches(isDisplayed()));

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withId(R.id.btSettings),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.appBarLayout),
                                        0),
                                2),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        ViewInteraction seekBar = onView(
                allOf(withId(R.id.seekbar),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        seekBar.check(matches(isDisplayed()));

        ViewInteraction appCompatImageButton3 = onView(
                allOf(withContentDescription("Desplazarse hacia arriba"),
                        childAtPosition(
                                allOf(withId(R.id.idAppBarSettings),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton3.perform(click());

        ViewInteraction appCompatImageButton4 = onView(
                allOf(withId(R.id.btProfile),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.appBarLayout),
                                        0),
                                0),
                        isDisplayed()));
        appCompatImageButton4.perform(click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.btnLogOut), withText("Cerrar sesión"),
                        childAtPosition(
                                allOf(withId(R.id.botonera),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                3)),
                                0),
                        isDisplayed()));
        appCompatButton2.perform(click());


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("post").whereEqualTo("post", "Prueba de espresso");
        query.get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {

                        for(QueryDocumentSnapshot q: task.getResult()){
                            Log.d("Test", q.toString());
                            q.getReference().delete();
                        }
                    }
                }
        );

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
