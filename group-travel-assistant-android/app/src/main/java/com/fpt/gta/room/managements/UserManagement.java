package com.fpt.gta.room.managements;

import android.app.Application;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;

import com.fpt.gta.room.daos.UserDao;
import com.fpt.gta.room.database.RMaPDatabase;
import com.fpt.gta.room.entities.User;


public class UserManagement {
//    private UserDao mUserDao;
//    private Application mApplication;
//
//    public UserManagement(Application application) {
//        this.mApplication = application;
//        RMaPDatabase vietAsianDatabase = RMaPDatabase.getDatabase(mApplication);
//        mUserDao = vietAsianDatabase.userDao();
//    }
//
//    public interface OnDataCallBackUser {
//        void onDataSuccess(User user);
//
//        void onDataFail();
//    }
//
//    public interface OnDataCallBackAccessToken {
//        void onDataSuccess(String accessToken);
//
//        void onDataFail();
//    }
//
//    public void deleteAllUser(OnDataCallBackUser listener) {
//        DeleteAllUserAsync deleteAsync = new DeleteAllUserAsync(mUserDao, listener);
//        deleteAsync.execute();
//    }
//
//    public void addUser(User customer, OnDataCallBackUser listener) {
//        AddUserAsync addCustomerAsync = new AddUserAsync(mUserDao, listener);
//        addCustomerAsync.execute(customer);
//    }
//
//    public void getCustomerInfo(OnDataCallBackUser listener) {
//        GetUserAsync getCustomerAsync = new GetUserAsync(mUserDao, listener);
//        getCustomerAsync.execute();
//    }
//
//    public void updateCustomer(User customer, OnDataCallBackUser listener) {
//        UpdateUserAsyn updateCustomerAsyn = new UpdateUserAsyn(mUserDao, listener);
//        updateCustomerAsyn.execute(customer);
//    }
//
//    public void getAccessToken(OnDataCallBackAccessToken listener) {
//        GetAccessTokenAsync getAccessTokenAsync = new GetAccessTokenAsync(mUserDao, listener);
//        getAccessTokenAsync.execute();
//    }
//
//    private class AddUserAsync extends AsyncTask<User, Void, Void> {
//        private UserDao mDaoAsync;
//        private OnDataCallBackUser mListener;
//
//        public AddUserAsync(UserDao mDaoAsync, OnDataCallBackUser listener) {
//            this.mDaoAsync = mDaoAsync;
//            this.mListener = listener;
//        }
//
//        @Override
//        protected Void doInBackground(User... users) {
//            try {
//                mDaoAsync.insertUser(users);
//            } catch (SQLiteConstraintException e) {
//
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            mListener.onDataSuccess(null);
//        }
//    }
//
//    private class UpdateUserAsyn extends AsyncTask<User, Void, Void> {
//        private UserDao mDaoAsync;
//        private int update;
//        private OnDataCallBackUser mListener;
//
//        public UpdateUserAsyn(UserDao mUserDao, OnDataCallBackUser listener) {
//            this.mDaoAsync = mUserDao;
//            this.mListener= listener;
//        }
//
//
//        @Override
//        protected Void doInBackground(User... users) {
//            try {
//                update=mDaoAsync.updateCustomer(users);
//            } catch (SQLiteConstraintException e) {
//                mListener.onDataFail();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            mListener.onDataSuccess(null);
//        }
//    }
//
//    private class GetUserAsync extends AsyncTask<User, Void, Void> {
//        private UserDao mDaoAsync;
//        private User mCustomer;
//        private OnDataCallBackUser mListener;
//
//        public GetUserAsync(UserDao mUserDaoAsync, OnDataCallBackUser mListener) {
//            this.mDaoAsync = mUserDaoAsync;
//            this.mListener = mListener;
//        }
//
//
//        @Override
//        protected Void doInBackground(User... customers) {
//            try {
//                mCustomer = mDaoAsync.getUserInfo();
//            } catch (SQLiteConstraintException e) {
//                mListener.onDataFail();
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            if (mCustomer != null) {
//                mListener.onDataSuccess(mCustomer);
//            } else {
//                mListener.onDataFail();
//            }
//        }
//    }
//
//    private class DeleteAllUserAsync extends AsyncTask<User, Void, Void> {
//        private UserDao mDaoAsync;
//        private OnDataCallBackUser mListener;
//
//        public DeleteAllUserAsync(UserDao mDaoAsync, OnDataCallBackUser mListener) {
//            this.mDaoAsync = mDaoAsync;
//            this.mListener = mListener;
//        }
//
//        @Override
//        protected Void doInBackground(User... customers) {
//            try {
//                mDaoAsync.deleleAllUser();
//            } catch (SQLiteConstraintException e) {
//
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            mListener.onDataSuccess(null);
//        }
//    }
//
//    private class GetAccessTokenAsync extends AsyncTask<User, Void, Void> {
//        private UserDao mDaoAsync;
//        private String mAccessToken;
//        private OnDataCallBackAccessToken mListener;
//
//        public GetAccessTokenAsync(UserDao customerDao, OnDataCallBackAccessToken mListener) {
//            this.mDaoAsync = customerDao;
//            this.mListener = mListener;
//        }
//
//
//        @Override
//        protected Void doInBackground(User... customers) {
//            try {
//                mAccessToken = mDaoAsync.getAccessToken();
//            } catch (SQLiteConstraintException e) {
//
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            if (mAccessToken != null) {
//                mListener.onDataSuccess(mAccessToken);
//            } else {
//                mListener.onDataFail();
//            }
//        }
//    }
}
