function [p1, p2] = mark_data_single_episode(acc)
    format long g
    skip_points = 20;
    %movavg_points = 5;
    window_len = 62;
    
    acc_mag = sqrt(sum(acc.*acc, 2)) - 9.806;
    %acc_mag = tsmovavg(acc_mag, 's', movavg_points, 1);    
    %acc_mag(1:movavg_points-1, :) = 0;
    
    if sum(isnan(acc_mag)) > 0
        fprintf('There is NaN in accelerometer data');        
        return;
    end       
    
    acc_mag = abs(acc_mag);
    
    th = 1;
    th_mean = 2;
    len = length(acc_mag);
        
    a = skip_points;
    while a <=len-window_len && mean(acc_mag(a:a+window_len)) < th_mean 
        %fprintf('%d %.2f', a, acc_mag(a));
        a = a + 1;        
    end  
    
     while a <=len && acc_mag(a) < th 
        %fprintf('%d %.2f', a, acc_mag(a));
        a = a + 1;        
    end   
    
    if a>=len
        fprintf('Error. Start mark poitn reach to end\n');
        fprintf('XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\n');
        return;
    end
        
    while a > 1 && acc_mag(a) > acc_mag(a-1)                     
        a = a -1;        
    end
       
    p1 = a;
   
    % calculate second cut point
    a = len;
    while a > skip_points+window_len && mean(acc_mag(a-window_len+1:a)) < th_mean
        a = a - 1;        
    end    
    
    while a > skip_points && acc_mag(a) < th
        a = a - 1;        
    end
    
    if a <= 1
        fprintf('Error. End mark poitn reach to start');
        fprintf('XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX');
        return;
    end
        
    while a <len && acc_mag(a) > acc_mag(a+1)                     
        a = a + 1;        
    end
    
    p2 = a;
   
end