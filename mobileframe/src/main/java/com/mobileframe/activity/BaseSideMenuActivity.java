package com.mobileframe.activity;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobileframe.R;
import com.mobileframe.common.ActivityStackManager;
import com.mobileframe.tools.DensityUtil;
import com.mobileframe.tools.ToastUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * author: tgl
 * 侧滑菜单基类,设置整体背景颜色直接调用mDrawerLayout
 * 对内容设置背景颜色调用layoutContent
 */
public abstract class BaseSideMenuActivity extends BaseActivity implements
        ExpandableListView.OnGroupClickListener, ExpandableListView.OnChildClickListener {

    protected DrawerLayout mDrawerLayout;

    //菜单布局父容器
    protected LinearLayout layoutLySideMenu;
    protected FrameLayout headView;

    //内容布局父容器
    protected LinearLayout layoutContent;
    protected FrameLayout footerView;

    /**
     * title容器，使用此layout是为了打开菜单是左面title动画
     */
    private Toolbar mToolbar;
    protected LinearLayout dotsBorder;

    private LinkedList<MainMenu> mMenuList;
    private ExpandableListView exListView;
    protected MenuAdapter menuAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_side_menu_base;
    }

    @Override
    protected boolean showTitleBarView() {
        return false;
    }

    private void init() {
        mMenuList = new LinkedList<>();
        menuAdapter = new MenuAdapter(mMenuList);
    }

    @Override
    public void initView() {
        init();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        layoutLySideMenu = (LinearLayout) findViewById(R.id.sideMenuLl);
        layoutLySideMenu.getLayoutParams().width = DensityUtil.getScreenW(this) / 2;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            layoutLySideMenu.setPadding(layoutLySideMenu.getPaddingLeft(), DensityUtil
//            .getStatusBarH(this),
//                    layoutLySideMenu.getPaddingRight(), layoutLySideMenu.getPaddingBottom());
//        }
        layoutContent = (LinearLayout) findViewById(R.id.sideContent);
        layoutContent.setBackgroundColor(ContextCompat.getColor(this, R.color.colorWitheT80));
        mToolbar = (Toolbar) findViewById(R.id.layoutToolbar);

        exListView = (ExpandableListView) findViewById(R.id.sideExpandableList);
        exListView.setAdapter(menuAdapter);

        mMenuList = initData(mMenuList);
        menuAdapter.setData(mMenuList, exListView);
        exListView.setOnChildClickListener(this);
        exListView.setOnGroupClickListener(this);
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
//                float scale = 1 - slideOffset;
//                float scaleHeight = 0.9f;//缩放之后所占高度：90%，1：整个高度（100%）
//                float rightScale = scaleHeight + scale * 0.1f;//
//                float leftScale = 1 - 0.3f * scale;
//                ViewHelper.setScaleX(mMenu, leftScale);
//                ViewHelper.setScaleY(mMenu, leftScale);
//                ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
//                ViewHelper.setTranslationX(mContent, mMenu.getMeasuredWidth() * (1 - scale));
//                ViewHelper.setPivotX(mContent, 0);
//                ViewHelper.setPivotY(mContent, mContent.getMeasuredHeight());
//                ViewHelper.setScaleX(mContent, rightScale);
//                ViewHelper.setScaleY(mContent, rightScale);
//                mContent.invalidate();


                View content = mDrawerLayout.getChildAt(0);
                float scale = 1 - slideOffset;//1~0
                float scaleHeight = 0.9f;//缩放之后所占高度：90%，1：整个高度（100%）
                float rightScale = scaleHeight + scale * 0.1f;//
                float leftScale = 1 - 0.3f * scale;
                drawerView.setScaleX(leftScale);//1~0.7
                drawerView.setScaleY(leftScale);//1~0.7

                content.setTranslationX(drawerView.getMeasuredWidth() * (1 - scale));//0~wid
                content.setPivotX(0);
                content.setPivotY(content.getMeasuredHeight());
                content.setScaleX(rightScale);
                content.setScaleY(rightScale);
                content.invalidate();
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        setMaterialLeftView(R.string.openMenu, R.string.closeMenu);
    }

    /**
     * 添加菜单头文件
     *
     * @param layoutResId
     * @return
     */
    protected View addMenuHeadView(int layoutResId) {
        headView = (FrameLayout) findViewById(R.id.sideHeaderFl);
        return getLayoutInflater().inflate(layoutResId, headView);
    }

    /**
     * 添加菜单脚文件
     *
     * @param layoutResId
     * @return
     */
    protected View addMenuFooterView(int layoutResId) {
        return getLayoutInflater().inflate(layoutResId, footerView);
    }

    protected abstract LinkedList<MainMenu> initData(LinkedList<MainMenu> mainMenuContentList);

    private long exitTime = 0;

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (System.currentTimeMillis() - exitTime > 2000) {
                exitTime = System.currentTimeMillis();
                ToastUtils.showToast(this, R.string.str_press_again);
            } else {
                ActivityStackManager.getInstance().finishAllActivity();
                super.onBackPressed();
            }
        }
    }

    /**
     * @param icon 为0时，不设置图标
     * @param name
     */
    protected void setCenterTitle(int icon, CharSequence name,
                                  View.OnClickListener onClickListener) {
        TextView title = (TextView) findViewById(R.id.textCenterToolbar);
        title.setVisibility(View.VISIBLE);
        if (onClickListener != null) {
            title.setOnClickListener(onClickListener);
        }
        title.setVisibility(View.VISIBLE);
        title.setText(name);
        if (icon != 0) {
            title.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(icon), null,
                    null, null);
        }
    }

    protected TextView getCenterTitle() {
        return (TextView) findViewById(R.id.textCenterToolbar);
    }

    /**
     * 使用Material Design 默认图标
     *
     * @param openStr
     * @param closeStr
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void setMaterialLeftView(int openStr, int closeStr) {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                openStr, closeStr);
        toggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

//    /**
//     * @return int[] 0:width,1:height
//     */
//    protected int[] getMaterialTitleViewSize() {
//        Rect rect = new Rect();
//        mToolbar.getDrawingRect(rect);
//
//        return new int[]{rect.width(), rect.height()};
//    }

    /**
     * @param icon            为0时，不设置图标
     * @param onClickListener
     */
    protected void setRightView(int icon, View.OnClickListener onClickListener) {
        this.setRightView(icon, "", onClickListener);
    }

    /**
     * @param onClickListener
     */
    protected void setRightView(CharSequence name, View.OnClickListener onClickListener) {
        this.setRightView(0, name, onClickListener);
    }

    /**
     * @param icon            为0时，不设置图标
     * @param name
     * @param onClickListener
     */
    protected void setRightView(int icon, CharSequence name, View.OnClickListener onClickListener) {
        TextView rightView = (TextView) findViewById(R.id.textRightToolbar);
        rightView.setVisibility(View.VISIBLE);
        if (onClickListener != null) {
            rightView.setOnClickListener(onClickListener);
        }
        rightView.setText(name);
        if (icon != 0) {
            rightView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(icon),
                    null, null, null);
        }
    }

    /**
     * @param icon            为0时，不设置图标
     * @param name
     * @param onClickListener
     */
    protected void setLeftView(int icon, CharSequence name, View.OnClickListener onClickListener) {
        TextView pageLeftView = (TextView) findViewById(R.id.textLeftToolbar);
        pageLeftView.setVisibility(View.VISIBLE);
        if (onClickListener != null) {
            pageLeftView.setOnClickListener(onClickListener);
        }
        pageLeftView.setText(name);
        if (icon != 0) {
            pageLeftView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(icon)
                    , null, null, null);
        }
    }

    private class MenuAdapter extends BaseExpandableListAdapter {

        private LinkedList<MainMenu> menuContentList;
        private int groupFocusItem, childFocusItem = 0x123456;

        public MenuAdapter(LinkedList<MainMenu> menuContentList) {
            this.menuContentList = menuContentList;
        }

        public void setData(LinkedList<MainMenu> menuContentList, ExpandableListView content) {
            if (menuContentList != null) {
                this.menuContentList = menuContentList;
                notifyDataSetChanged();
                //默认展开子项
                for (int i = 0; i < menuContentList.size(); i++) {
                    content.expandGroup(i);
                }
            }
        }

        public void setFocusItem(int groupFocusItem, int childFocusItem) {
            this.groupFocusItem = groupFocusItem;
            this.childFocusItem = childFocusItem;
            notifyDataSetChanged();
        }

        @Override
        public int getGroupCount() {
            return menuContentList.size();
        }

        @Override
        public int getChildrenCount(int i) {
            return menuContentList.get(i).getChildMenu().size();
        }

        @Override
        public MainMenu getGroup(int groupPosition) {
            return menuContentList.get(groupPosition);
        }

        @Override
        public MainMenu getChild(int groupPosition, int childPosition) {
            return menuContentList.get(groupPosition).getChildMenu().get(childPosition);
        }

        @Override
        public long getGroupId(int groupId) {
            return groupId;
        }

        @Override
        public long getChildId(int groupId, int childId) {
            return childId;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(final int groupPosition, boolean b, View convertView,
                                 ViewGroup parent) {
            final MainMenu menuBean = getGroup(groupPosition);
            ViewHolder holder;
            if (convertView == null) {
                convertView =
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_side_menu,
                                parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            int type = menuBean.getType();
            switch (type) {
                case MainMenu.TYPE_NO_ICON:
                    holder.name.setText(menuBean.getName());
                    break;

                case MainMenu.TYPE_HINT:
                    holder.name.setText(menuBean.getName());
                    holder.name.setCompoundDrawablesWithIntrinsicBounds(ContextCompat
                                    .getDrawable(parent.getContext(), menuBean.getIcon()), null,
                            ContextCompat.getDrawable(parent.getContext(), menuBean.getHint()),
                            null);
                    break;

                case MainMenu.TYPE_NORMAL:
                    holder.name.setText(menuBean.getName());
                    holder.name.setCompoundDrawablesWithIntrinsicBounds(
                            ContextCompat.getDrawable(parent.getContext(), menuBean.getIcon()),
                            null, null, null);
                    break;
                case MainMenu.TYPE_SEPARATOR:
                    holder.name.setVisibility(View.GONE);
                    holder.separator.setVisibility(View.VISIBLE);
                    break;
            }
            convertView.setClickable(false);
            return convertView;
        }

        @Override
        public View getChildView(final int i, int i1, boolean b, View convertView,
                                 ViewGroup parent) {
            final MainMenu menuBean = getChild(i, i1);
            ViewHolder holder;
            if (convertView == null) {
                convertView =
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_side_menu,
                                parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (groupFocusItem == i && childFocusItem == i1) {//被选中项颜色
                holder.name.setBackgroundColor(Color.parseColor("#0cbff0"));
                holder.name.setTextColor(Color.WHITE);
            } else {
                holder.name.setBackgroundColor(Color.TRANSPARENT);
                holder.name.setTextColor(Color.WHITE);
            }
            int type = menuBean.getType();
            switch (type) {
                case MainMenu.TYPE_NO_ICON:
                    holder.name.setText(menuBean.getName());
                    break;

                case MainMenu.TYPE_HINT:
                    holder.name.setText(menuBean.getName());
                    holder.name.setCompoundDrawablesWithIntrinsicBounds(
                            ContextCompat.getDrawable(parent.getContext(), menuBean.getIcon()),
                            null, ContextCompat.getDrawable(parent.getContext(),
                                    menuBean.getHint()), null);
                    break;

                case MainMenu.TYPE_NORMAL:
                    holder.name.setText(menuBean.getName());
                    holder.name.setCompoundDrawablesWithIntrinsicBounds(
                            ContextCompat.getDrawable(parent.getContext(), menuBean.getIcon()),
                            null, null, null);
                    break;
                case MainMenu.TYPE_SEPARATOR:
                    holder.name.setVisibility(View.GONE);
                    holder.separator.setVisibility(View.VISIBLE);
                    break;
            }
            holder.name.setPadding(DensityUtil.dip2px(parent.getContext(), 10),
                    DensityUtil.dip2px(parent.getContext(), 5), 0,
                    DensityUtil.dip2px(parent.getContext(), 5));
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }

        private class ViewHolder {

            private TextView name;
            private View separator;

            public ViewHolder(View rootView) {
                name = (TextView) rootView.findViewById(R.id.main_menu_name);
                separator = rootView.findViewById(R.id.main_menu_separator);
            }
        }
    }

    public static class MainMenu {

        private int icon;
        private int type;
        private int hint;
        private CharSequence name;
        private List<MainMenu> childMenu;
        /**
         * 只有文字
         */
        private static final int NO_ICON = 0x0;
        private static final int N0_HINT = 0x1;

        /**
         * 有图标、有文字，没有提示
         */
        public static final int TYPE_NORMAL = 0;
        /**
         * 只有文字
         */
        public static final int TYPE_NO_ICON = 1;
        /**
         * 只有分割线
         */
        public static final int TYPE_SEPARATOR = 2;
        /**
         * 有图标、有文字、有提示
         */
        public static final int TYPE_HINT = 3;

        public MainMenu() {
            this(null, null);
        }

        public MainMenu(CharSequence name, List<MainMenu> childMenu) {
            this(name, NO_ICON, childMenu);
        }

        public MainMenu(CharSequence name, int icon) {
            this(name, icon, N0_HINT, null);
        }

        public MainMenu(CharSequence name, int icon, List<MainMenu> childMenu) {
            this(name, icon, N0_HINT, childMenu);
        }

        public MainMenu(CharSequence name, int icon, int hint, List<MainMenu> childMenu) {
            if (childMenu == null) {
                childMenu = new ArrayList<>();
            }
            this.childMenu = childMenu;
            this.name = name;
            this.icon = icon;
            this.hint = hint;
            if (icon == NO_ICON && TextUtils.isEmpty(name)) {
                type = TYPE_SEPARATOR;
            } else if (icon == NO_ICON) {
                type = TYPE_NO_ICON;
            } else if (hint == N0_HINT) {
                type = TYPE_NORMAL;
            } else {
                type = TYPE_HINT;
            }

            if (type != TYPE_SEPARATOR && TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException("You need set a name for a non-separator item");
            }

        }

        public int getIcon() {
            return icon;
        }

        public int getType() {
            return type;
        }

        public CharSequence getName() {
            return name;
        }

        public int getHint() {
            return hint;
        }

        public List<MainMenu> getChildMenu() {
            return childMenu;
        }
    }

}
