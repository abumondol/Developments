for sub = 33:39
    ind = csvread(strcat('test_data/', num2str(sub)));
    ind = ind(:, end);
    Yfit = csvread(strcat('res/', num2str(sub)));
    %Yfit = res{1, sub}.Yfit;
    %Yfit = cell2mat(Yfit);
    %Yfit = str2num(Yfit);
    %scores = res{1, sub}.scores(:,2);
    
    Yfit = Yfit(:, 2);
    accel = data(sub).accel;
    %result = [accel(ind, 1), Yfit];
    
    result = [];
    ind_len = length(ind);
    i = 3;
    while i <= ind_len-2
        selected = false;
        if Yfit(i) > 0.5
            [m, I] = max(Yfit(i-2:i+2));
            if I == 3
                result = [result; accel(ind(i),1), 1];                
                selected = true;
            end
        end
        
        if selected
            i = i + 3;
        else
            i = i + 1;
        end
        
    end
    
    
    
    annots = data(sub).annots;
    if ~isempty(annots)
        ai = annots; %get_annot_indices(accel, annots);
        bi = ai(ai(:,2)<400, :);
        di = ai(ai(:,2)>=400 & ai(:,2)<1000,  :);
        ni = ai(ai(:,2)>=1000,  :);

        bi(:,2) =1;
        di(:,2) = 1;
        ni(:,2) = 1;
    end

    count = length(accel);
    last_time = accel(end, 1);
    step = 600;
    for i=0:step:last_time%count    
        close all
        %figure
        figure('units','normalized','outerposition',[0 0 1 1]);
        r = result(result(:,1)>=i & result(:,1)< i+step, :);
        if ~isempty(r)        
            scatter(r(:,1), r(:,2), 'rx');
            title(num2str(sub));
            hold on
            %scatter(r(:,1), r(:,3), 'r.');
            xlim([i, i+step]);
            ylim([-0.1, 1.1]);

        end

        if isempty(annots)
            waitforbuttonpress
            continue
        end

        b = bi(bi(:,1)>=i & bi(:,1)< i+step, :);
        if ~isempty(b)        
            scatter(b(:,1), b(:,2), 'bo');
            hold on
        end

        d = di(di(:,1)>=i & di(:,1)< i+step, :);
        if ~isempty(d)
            scatter(d(:,1), d(:,2),  'go');
            hold on
        end

        n = ni(ni(:,1)>=i & ni(:,1)< i+step, :);
        if ~isempty(n)
            scatter(n(:,1), n(:,2), 'yo');
        end

        waitforbuttonpress
    end
end


