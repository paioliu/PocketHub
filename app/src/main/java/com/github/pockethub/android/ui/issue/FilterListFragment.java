/*
 * Copyright (c) 2015 PocketHub
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.pockethub.android.ui.issue;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.github.pockethub.android.R;
import com.github.pockethub.android.core.issue.IssueFilter;
import com.github.pockethub.android.persistence.AccountDataManager;
import com.github.pockethub.android.ui.ItemListFragment;
import com.github.pockethub.android.ui.item.issue.IssueFilterItem;
import com.github.pockethub.android.util.AvatarLoader;
import com.github.pockethub.android.util.InfoUtils;
import com.xwray.groupie.Item;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

import static java.lang.String.CASE_INSENSITIVE_ORDER;

/**
 * Fragment to display a list of {@link IssueFilter} objects
 */
public class FilterListFragment extends ItemListFragment<IssueFilter> implements Comparator<IssueFilter> {

    @Inject
    protected AccountDataManager cache;

    @Inject
    protected AvatarLoader avatars;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(R.string.no_bookmarks);
    }

    @Override
    protected Single<List<IssueFilter>> loadData(boolean forceRefresh) {
        return Single.fromCallable(() -> new ArrayList<>(cache.getIssueFilters()))
                .flatMap(filters -> Observable.fromIterable(filters)
                        .sorted(FilterListFragment.this)
                        .toList());
    }

    @Override
    protected Item createItem(IssueFilter item) {
        return new IssueFilterItem(avatars, item);
    }

    @Override
    public void onItemClick(@NonNull Item item, @NonNull View view) {
        if (item instanceof IssueFilterItem) {
            IssueFilter filter = ((IssueFilterItem) item).getData();
            startActivity(IssueBrowseActivity.createIntent(filter));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        refresh();
    }

    @Override
    protected int getErrorMessage() {
        return R.string.error_bookmarks_load;
    }

    @Override
    public int compare(final IssueFilter lhs, final IssueFilter rhs) {
        int compare = CASE_INSENSITIVE_ORDER.compare(InfoUtils.createRepoId(lhs.getRepository()),InfoUtils.createRepoId(rhs.getRepository()));
        if (compare == 0) {
            compare = CASE_INSENSITIVE_ORDER.compare(
                    lhs.toDisplay().toString(), rhs.toDisplay().toString());
        }
        return compare;
    }
}
