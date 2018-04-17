function str = time2str(s)
    s = round(s,0);
    m = floor(s/60);
    s = s-m*60;   
    h = floor(m/60);
    m = m - h*60;
    
    if s < 10
        s = strcat('0',num2str(m));
    else
        s = num2str(s);
    end
    
    if m < 10
        m = strcat('0', num2str(m));
    else
        m = num2str(m);
    end
    
    if h==0
        str = strcat(m,':',s);
    elseif h<10
        str = strcat('0',num2str(h),':',m,':',s);
    else
        str = strcat(num2str(h),':',m,':',s);
    end
        
    
end