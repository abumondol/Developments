sub_count = 39;
res_count = zeros(sub_count, 3);
for sub = 1:sub_count
    fprintf('Subject: %d\n', sub);
    ind = csvread(strcat('test_data/', num2str(sub)));
    ind = ind(:, end);
    Yfit = csvread(strcat('res/', num2str(sub)));        
    Yfit = Yfit(:, 2);
    accel = data(sub).accel;
    
    result = [];
    ind_len = length(ind);
    i = 3;
    while i <= ind_len-2
        selected = false;
        if Yfit(i) > 0.5
            [m, I] = max(Yfit(i-2:i+2));
            if I == 3
                result = [result; accel(ind(i),1)];                
                selected = true;
            end
        end
        
        if selected
            i = i + 3;
        else
            i = i + 1;
        end        
    end
    
    TP = 0;
    FP = 0;
    FN = 0;
    
    a = data(sub).annots_adjusted;
    
    if isempty(a)
        TP = 0;
        FP = length(result);
        FN = 0;
        res_count(sub,:) = [TP, FP, FN];
        continue
    end
    
    a = a(a(:,2)<1000, :);
    a = [a(:,1)-2, a(:,1)+2];    
    
    len = length(result);    
    for i = 1:len        
        r = result(i);        
        x = find(r>= a(:,1) & r<= a(:,2));
        if isempty(x)
            FP = FP +1;
        else
            TP = TP +1;
        end        
    end
    
    annot = data(sub).annots_adjusted;
    annot = annot(annot(:,2)<400, :);
    r = [result(:,1)-2, result(:,1)+2];
    len = length(annot);
    for i = 1:len
        a = annot(i, 1);        
        x = find(a>= r(:,1) & a<= r(:,2));
        if isempty(x)
            FN = FN +1;        
        end        
    end
    
    res_count(sub,:) = [TP, FP, FN];
end
TP = res_count(:,1);
FP = res_count(:,2);
FN = res_count(:,3);

P = TP./(TP+FP);
R = TP./(TP+FN);
F1 = 2*(P.*R)./(P+R);

res_count = [res_count, P, R, F1];


