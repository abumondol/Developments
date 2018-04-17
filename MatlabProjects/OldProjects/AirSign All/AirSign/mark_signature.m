function [p1, p2] = mark_signature(acc)
    format long g
    skip_points = 20;
    acc_mag = acc(skip_points:end-skip_points,4);    
    acc_mag = zscore(acc_mag);   
    acc_mag = abs(acc_mag);
    th = 0.5;   
    len = size(acc_mag,1);
        
    a = 1;
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
       
    p1 = a + skip_points;
   
    % calculate second cut point
    a = len;
    while a > 1 && acc_mag(a) < th                     
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
    
    p2 = a+skip_points;
   
end