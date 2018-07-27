% 
% This function takes two joint configurations and the parameters of the
% obstacle as input and calculates whether a collision free path exists
% between them.
% 
% input: q1, q2 -> start and end configuration, respectively. Both are 1x4
%                  vectors.
%        sphereCenter -> 3x1 position of center of sphere
%        r -> radius of sphere
%        rob -> SerialLink class that implements the robot
% output: collision -> binary number that denotes whether this
%                      configuration is in collision or not.

function collision = Q1(rob,q1,q2,sphereCenter,r)
    deltaQ = q2 - q1;
    deltaQ = (deltaQ)/50;
    alpha = 1;
    qNext = q1;
    i= 1;
    while(i <= 50)
       qNext = qNext + (alpha * deltaQ);
        % Collision check for all sampled orientations 
       collision = robotCollision(rob,qNext,sphereCenter,r);
       if (or ((collision == 1), (qNext >= q2)))
           break;
       end
       i = i+1;
    end
end

