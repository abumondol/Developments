function r = get_subject_data_range(sid)

    if sid<=15
        r = [sid, sid];
    elseif sid == 16
        r = [16, 17];
    elseif sid == 17
        r = [18, 22];
    elseif sid == 18
        r = [23, 24];
    elseif sid == 19
        r = [25, 26];
    elseif sid ==20
        r = [27, 30];
    elseif sid == 21
        r = [31, 35];
    elseif sid == 22
        r = [36, 36];
    elseif sid >= 37 && sid <= 52            
        r = [sid, sid];
    else
        r = [0, 0];
    end
        

end