load('C:\ASM\DevData\eating\matlab\steven_data_results');

for s = 1:11
    for sess = 1:2
        accel = data(s).session(sess).accel;
        meals = data(s).session(sess).meals;
        meals = meals(meals(:,end)<=3, :);
        
        res0 = data(s).session(sess).retrain(1).res;
        res1 = data(s).session(sess).retrain(2).res;
        %res2 = data(s).session(sess).retrain(3).res;
        %res3 = data(s).session(sess).retrain(4).res;
        
        t = accel(:,1);
        a = accel(:, 2:4);        
        %e = get_energy(a, 15*30);
        res = res1;
        
        figure
        scatter(res(:,2) , res0(:, 3), '.g');
        hold on        
        scatter(res(:,2) , res0(:, 4), '.r');
        scatter(res(:,2) , res0(:, 5), '.b');
        
        plot([t(1);t(end)], [0.5;0.5], '-');
        plot([t(1);t(end)], [0;0]);
        plot([t(1);t(end)], [1;1], '-g');
        
        count = size(meals, 1);
        for i = 1:count
            t1 = t(meals(i, 1));
            t2 = t(meals(i, 2));
            scatter(t1, 0, 'bd');
            scatter(t2, 0, 'rx');
            plot([t1;t1], [-10;10], '-b');
            plot([t2;t2], [-10;10], 'r');
        end
        
    end
end
