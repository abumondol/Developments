load('data');
res = [];

for sub=1:5
    for sess=1:2
        fprintf('Subject: %d, Session:%d\n', sub, sess);        
        a = data(sub).session(sess).annotations;
        %a = a(:,1);
        a = a(a(:,2)<1000, 1);
        
        diff = a(2:end,:) - a(1:end-1, :);
        if sub==3 && sess==1
            diff;
        end
        
        count = length(diff);
        ind = 1:count;
        sb = zeros(count,1)+sub;
        ss = zeros(count,1)+sess;        
        res = [res; diff, ind', sb, ss];        
    end
end

res = sortrows(res);



