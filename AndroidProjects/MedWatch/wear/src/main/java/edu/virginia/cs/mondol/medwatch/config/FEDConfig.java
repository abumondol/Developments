package edu.virginia.cs.mondol.medwatch.config;

/**
 * Created by Abu on 3/13/2017.
 */

public class FEDConfig {

    public int home_id = 0;
    public int version = 5 * 1000;
    public int ble_initial_wait_time = 5 * 1000;
    public int ble_data_save_interval = 10 * 60 * 1000;
    public int ble_scan_interval = 105 * 1000;
    public int ble_scan_duration = 15 * 1000;
    public int time_sync_repeat_try_interval = 30 * 1000;
    public int time_sync_min_response_time = 1000;
    public int max_battery_send_interval = 60 * 60 * 1000;

    public int alarm_hour = 15;
    public int alarm_minute = 30;

    public int net_max_net_enable_try_count = 3;
    public int net_max_router_try_count = 5;
    public int net_max_ip_try_count = 5;
    public int net_max_server_try_count = 3;
    public int net_interval_while_upload_download_working = 20 * 1000;
    public int net_interval_between_net_enable_try = 5 * 1000;
    public int net_interval_between_router_try = 10 * 1000;
    public int net_interval_between_ip_try = 10 * 1000;
    public int net_interval_between_runnables = 20 * 1000;
    public int net_interval_initial_runnable = 3 * 1000;


}
