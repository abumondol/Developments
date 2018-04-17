function [p1, p2] = mark_signature_old(acc)
    format long g
    acc_mag = acc(:,4);
    p1 = 1;
    p2 = 1;
    g = 9.8;
    eps1 = 0.1;
    eps2 = 0.5;
    th_up1 = g + eps1;
    th_down1 = g - eps1;
    th_up2 = g + eps2;
    th_down2 = g - eps2;
    len = size(acc_mag,1);
    
    a = 20;
    while a <=len && acc_mag(a,1) < th_up2 && acc_mag(a,1) > th_down2             
        %fprintf('a mag: %d %.2f\n', a, acc_mag(a));
        a = a + 1;        
    end
    %fprintf('First up a, mag: %d, %.2f\n',a, acc_mag(a));
    
    if a>len
        fprintf('Error. Start mark poitn reach to end');
        fprintf('XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX');
        return;
    end    
    
    if acc_mag(a) >= th_up2        
        while  a >= 1 && acc_mag(a) > th_up1
            %fprintf('a mag: %d %.2f\n', a, acc_mag(a));
            a = a - 1;        
        end        
    else
        while  a >= 1 && acc_mag(a) < th_down1
            %fprintf('a mag: %d %.2f\n', a, acc_mag(a));
            a = a - 1;        
        end        
    end
    p1 = a + 1;
    
    %fprintf('First down a, mag: %d, %.2f\n',a, acc_mag(a));
        
    % calculate second cut point
    a=len-30;    
    while a > p1 && acc_mag(a) < th_up2 && acc_mag(a) > th_down2
        %fprintf('a mag: %d %.2f\n', a, acc_mag(a));
        a = a - 1;        
    end
    
    if a == p1
        fprintf('Error. End mark point reach to start mark point');
        return;
    end
    
    %fprintf('Reverse up a, mag: %d, %d\n',a, acc_mag(a));
    
    if acc_mag(a) >= th_up2
        while  a <= len && acc_mag(a) > th_up1
            %fprintf('a mag: %d %.2f\n', a, acc_mag(a));
            a = a + 1;        
        end        
    else
        while  a <= len && acc_mag(a) < th_down1
            %fprintf('a mag: %d %.2f\n', a, acc_mag(a));
            a = a + 1;        
        end        
    end
    p2 = a - 1;
    
    %fprintf('Reverse down a, mag: %d %d\n',a, acc_mag(a));
   
end