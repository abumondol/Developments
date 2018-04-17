close all; clc;

sub_count = length(hw);
r = 0.95;
[x,y,z] = sphere(20);
format long g
 
for i=4:4
    episode_count = length(hw(i).data);
    
    for j= 1:episode_count
        data = hw(i).data(j).raw_data;
        data = data(data(:,2)==11, :);
        q0 = data(:,7);
        q1 = data(:,4);
        q2 = data(:,5);
        q3 = data(:,6);
        
        Rx = [q0.*q0+q1.*q1-q2.*q2-q3.*q3, 2*(q1.*q2 - q0.*q3),  2*(q1.*q3+q0.*q2)];
        Ry = [2*(q0.*q3+q1.*q2),  q0.*q0-q1.*q1+q2.*q2-q3.*q3, 2*(q2.*q3 - q0.*q1)];
        Rz = [2*(q1.*q3 - q0.*q2), 2*(q2.*q3+q0.*q1), q0.*q0-q1.*q1-q2.*q2+q3.*q3 ];
        
        t = strrep(hw(i).data(j).file_name,'_', ', ');         
        grav = hw(i).data(j).alg(:,8:10);
        grav = round(grav/9.806,2);
        
        figure;        
        mesh(x*r, y*r, z*r); % where (a,b,c) is center of the sphere
        hold on
        plot3(grav(:,1), grav(:,2), grav(:,3));
        xlabel('X');
        ylabel('Y');
        zlabel('Z');
        title(strcat('Gravity: ',t));
        hold off
        
        
%         grav = round(Rx,2);
%         figure;        
%         mesh(x*r, y*r, z*r); % where (a,b,c) is center of the sphere
%         hold on
%         plot3(grav(:,1), grav(:,2), grav(:,3));
%         xlabel('X');
%         ylabel('Y');
%         zlabel('Z');
%         title(strcat('Quat Gravity Rx: ',t));
%         hold off
%         
%         grav = round(Ry,2);
%         figure;        
%         mesh(x*r, y*r, z*r); % where (a,b,c) is center of the sphere
%         hold on
%         plot3(grav(:,1), grav(:,2), grav(:,3));
%         xlabel('X');
%         ylabel('Y');
%         zlabel('Z');
%         title(strcat('Quat Gravity Ry: ',t));
%         hold off
%         
%         grav = round(Rz,2);
%         figure;        
%         mesh(x*r, y*r, z*r); % where (a,b,c) is center of the sphere
%         hold on
%         plot3(grav(:,1), grav(:,2), grav(:,3));
%         xlabel('X');
%         ylabel('Y');
%         zlabel('Z');
%         title(strcat('Quat Gravity Rz: ',t));
%         hold off
    end
end